package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tuk.shdelivery.Activity.HomeActivity
import com.tuk.shdelivery.Data.*
import com.tuk.shdelivery.R
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.databinding.*
import java.util.*


class ChatListFragment : Fragment() {

    val intent by lazy { requireActivity().intent }
    val binding by lazy { FragmentChatListBinding.inflate(layoutInflater) }
    lateinit var matchId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterChatRoom {}

        //나가기 버튼 리스너
        createExitButtonListener()

        //주문 수락 버튼 리스너
        orderAceptButtonListener()

        //배달 시작 리스너
        binding.deliveryStart.setOnClickListener {
            MatchDao.deliveryStart((intent.getSerializableExtra("user") as User).participateMatchId) {
            }
        }

        //보내기 버튼 활성, 비활성화
        sendButtonSetting(binding.input, binding.send)

        //주문수락 버튼 활성, 비활성화
        sendButtonSetting(binding.inputPoint, binding.orderAccept)

        //메세지 보내기 리스너
        createSendListener()

        //다운 스크롤 버튼 리스너
        createDownScrollButtonListener()

        //배달완료 버튼 리스너
        binding.deliveryComplite.setOnClickListener {
            binding.deliveryComplite.isEnabled = false
            val user = intent.getSerializableExtra("user") as User
            MatchDao.deliveryComplite(user) {
                    //!!!배달 완료 메세지 보내기
            }
        }
    }

    fun deliveryStart() {
        //방장이 배달 시작을 눌렀을 때
        binding.orderAccept.visibility = View.GONE
        binding.deliveryStart.visibility = View.GONE
        binding.inputPoint.isEnabled = false
        binding.deliveryComplite.visibility = View.VISIBLE
        Toast.makeText(context, "배달에 대해 말해 주세요!", Toast.LENGTH_SHORT).show()


        val user = intent.getSerializableExtra("user") as User
        //모두 배달 완료 리스너 달기
        MatchDao.deliveryCompliteListener(user) {
            //방 지우기
            MatchDao.removeMatchRoom(user.participateMatchId) {}
            val snackbar = Snackbar.make(binding.root, "맛있게 드세요!", Snackbar.LENGTH_SHORT)
            snackbar.setAction("닫기") { snackbar.dismiss() }.show()
        }
    }

    private fun orderAceptButtonListener() {
        binding.orderAccept.setOnClickListener {
            binding.orderAccept.isEnabled = false
            binding.inputPoint.isEnabled = false

            var text = ""
            var point = 0

            val user = intent.getSerializableExtra("user") as User
            //!!! 주문 수락 눌렀을때
            if (binding.orderAccept.text.toString() == "주문 수락") {

                //보유포인트보다 많이 입력하면 토스트 띄우기
                val userPoint = user.userPoint
                if (userPoint < binding.inputPoint.text.toString().toInt() || userPoint == 0L) {
                    Toast.makeText(requireContext(), "보유 포인트보다 많이 적을 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                    binding.orderAccept.isEnabled = true
                    binding.inputPoint.isEnabled = true
                    return@setOnClickListener
                }
                binding.orderAccept.text = "주문 취소"
                text = "주문 수락"

                point = binding.inputPoint.text.toString().toInt()

                MatchDao.orderUserPlus2(user) {}

            }
            //주문 취소 눌렀을때
            else {
                binding.orderAccept.text = "주문 수락"
                text = "주문 취소"

                point = binding.inputPoint.text.toString().toInt() * -1

                MatchDao.orderUserMisnus2(user) {}

            }
            binding.inputPoint.isEnabled = binding.orderAccept.text.toString() == "주문 수락"
            var orderChat = String.format(
                "%sP 만큼 %s 하였습니다.", binding.inputPoint.text.toString().toInt().toString(), text
            )
            //주문 수락|취소 Chat 만들기
            val chat = Chat(
                text, user.userName, orderChat, Calendar.getInstance().timeInMillis
            )

            MatchDao.sendMessageToFirebase(
                chat, user.participateMatchId
            ) {}

            user.userPoint -= point
            user.matchPoint = if (point > 0) point.toLong() else 0L
            intent.putExtra("user", user)
            MatchDao.updateOrderPoint(user.participateMatchId, point) {
                val updateFields = mapOf("userPoint" to user.userPoint, "matchPoint" to user.matchPoint)
                UserDao.updateUserFields(user.userId, updateFields) {
                    binding.orderAccept.isEnabled = true
                }
            }
//            MatchDao.updateOrderPoint(user.participateMatchId, point) {
//                UserDao.updateUser(user) {
//                    binding.orderAccept.isEnabled = true
//                }
//            }
        }
    }

    private fun createExitButtonListener() {
        var clickEnabled = true
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.exit -> {
                    val user = intent.getSerializableExtra("user") as User

                    // 방 주인인지 판단
                    val isOwner = user.userId == user.participateMatchId

                    // 매칭방에 참가 중인지 판단
                    val isParticipating = user.participateMatchId != ""

                    // 현재 배달 중인지 판단
                    val isDeliverStart = if (isParticipating) {
                        (intent.getSerializableExtra("matchRoomData") as MatchRoomData).id == "start"
                    } else false

                    val isOrderAccept = binding.orderAccept.text == "주문 취소"

                    when {
                        //클릭이 끝나지 않았다면
                        !clickEnabled -> {
                            Toast.makeText(context, "삭제중입니다.", Toast.LENGTH_SHORT).show()
                        }
                        // 배달중이라면
                        isDeliverStart -> {
                            Toast.makeText(
                                context, "배달을 완료하세요!! \n문제발생시 문의사항으로 연락주세요", Toast.LENGTH_SHORT
                            ).show()
                        }
                        // 방 주인이라면
                        isOwner -> {
                            Toast.makeText(
                                activity, "방을 생성한 사람이 나갈 수 없습니다.(방을 삭제해 주세요)", Toast.LENGTH_SHORT
                            ).show()
                        }
                        //주문 수락상태에서 나갈 수 없음
                        isOrderAccept -> {
                            Toast.makeText(
                                activity, "주문 수락 중입니다!", Toast.LENGTH_SHORT
                            ).show()
                        }
                        // 매칭방에 참가 중이라면 퇴장하고 퇴장 메세지 띄우기
                        isParticipating -> {
                            clickEnabled = false
                            MatchDao.exitUser(user) {
                                val chat = Chat(
                                    "퇴장",
                                    user.userName,
                                    "님이 퇴장하였습니다.",
                                    Calendar.getInstance().timeInMillis
                                )
                                MatchDao.sendMessageToFirebase(chat, user.participateMatchId) {
                                    outSettingChatRoom(true)
                                }
                                Handler().postDelayed({
                                    clickEnabled = true // 일정 시간 후 다시 터치 이벤트 허용
                                }, 2000)
                            }
                        }
                        // 아무것도 해당하지 않는 경우
                        else -> {
                            val snackbar =
                                Snackbar.make(binding.root, "매칭방을 찾아보세요!", Snackbar.LENGTH_SHORT)
                            snackbar.setAction("알겠습니다") { snackbar.dismiss() }.show()
                        }
                    }
                }
            }
            true
        }
    }

    fun outSettingChatRoom(isExists: Boolean) {
        val user = intent.getSerializableExtra("user") as User
        //주문 수락 상태 + 모두 수락버튼을 누르지 않았을때 + 매칭방이 존재 할때 주문 취소를 누르고 나간다.
        if (binding.orderAccept.text == "주문 취소" && binding.deliveryComplite.visibility == View.GONE && isExists) {
            binding.orderAccept.performClick()
        }

        //모든 채팅 제거
        binding.chatLayout.removeAllViews()
        //채팅 가림막 올리기
        binding.nochat.visibility = View.VISIBLE

        binding.inputPoint.text = null
        binding.inputPoint.isEnabled = true
        binding.orderAccept.visibility = View.GONE
        binding.orderAccept.isEnabled = true
        binding.orderAccept.text = "주문 수락"
        binding.deliveryComplite.isEnabled = true
        binding.deliveryComplite.visibility = View.GONE
        MatchDao.removeListener(user.participateMatchId)
        user.participateMatchId = ""
        //만약 포인트를 걸고 있다면 반환 한다.
        MatchDao.isRedemptionPoint(user){
            //배달완료를 누른적 없고 걸었던 포인트가 있다면 회수한다.
            if(it == null){
                if(user.matchPoint != 0L)
                    user.userPoint += user.matchPoint
            }
            //배달완료를 눌렀었다면 회수한다.
            else{
                Toast.makeText(context,"포인트가 정산 되었습니다!",Toast.LENGTH_SHORT).show()
                user.userPoint += it!!.toLong()
                MatchDao.deleteRedemptionPoint(user){}
            }
            user.matchPoint = 0L
            intent.putExtra("user", user)
            val fieldsToUpdate = mapOf(
                "userPoint" to user.userPoint,
                "matchPoint" to user.matchPoint,
                "participateMatchId" to user.participateMatchId
            )
            UserDao.updateUserFields(user.userId, fieldsToUpdate) {
                updateSubTitle()
                ((activity as HomeActivity).listFragment[0] as HomeFragment).reFresh()
            }
//            UserDao.updateUser(user) {
//                updateSubTitle()
//                ((activity as HomeActivity).listFragment[0] as HomeFragment).reFresh()
//            }
        }
    }

    public fun enterChatRoom(callback: () -> Unit) {
        binding.inputPoint.text.clear()
        //채팅방에 접속중인 사람이면 생성시 리스너 달기
        val user = intent.getSerializableExtra("user") as User
        matchId = user.participateMatchId


        //입장중이었다면
        if (matchId != "") {
            MatchDao.isMatchExists(matchId) { isExists ->
                //매칭방이 존재 한다면 들어가는 액션
                if (isExists) {
                    MatchDao.getParticipatingMatch(matchId) { matchRoomData ->
                        MatchDao.getChatRoomData(matchId) {
                            //프로필 설정
                            ((activity as HomeActivity).listFragment[2] as MypageFragment).SetProfile(
                                user
                            )
                            intent.putExtra("matchRoomData", matchRoomData)
                            intent.putExtra("chatRoomData", it)

                            //주문 수락 중이고, 배달 상태가 아니라면
                            if (user.matchPoint != 0L) {
                                binding.inputPoint.setText(user.matchPoint.toString())
                                binding.inputPoint.isEnabled = false
                                binding.orderAccept.isEnabled = true
                                binding.orderAccept.text = "주문 취소"

                                //배달중 상태라면
                                if (matchRoomData!!.id == "start") {
                                    binding.orderAccept.visibility = View.GONE
                                    binding.deliveryComplite.visibility = View.VISIBLE
                                    deliveryStart()
                                }
                            }
                            //모든 채팅을 불러오고 새로운 메세지 올때 리스너 등록
                            addNewMessageListener(matchId) {}

                            //주문 수락 리스너
                            MatchDao.addOrderAcceptListener(matchId) { chatRoomData ->
                                updateSubTitle(
                                    orderAcceptNum = chatRoomData.orderAcceptNum.toString(),
                                    participatePeopleSize = chatRoomData.participatePeopleId.size.toString()
                                )
                                //인원이 변경될 때
                                if ((intent.getSerializableExtra("chatRoomData") as ChatRoom).participatePeopleId.size != chatRoomData.participatePeopleId.size) {
                                    ((activity as HomeActivity).listFragment[2] as MypageFragment).SetProfile(
                                        user
                                    )
                                }
                                //방장이고, 배달중이 아닐떄
                                if (user.userId == user.participateMatchId && (intent.getSerializableExtra(
                                        "matchRoomData"
                                    ) as MatchRoomData).id != "start"
                                ) {
                                    //모두 주문수락 누르면 배달시작버튼 활성화
                                    //모두 주문수락 안눌렀다면 배달시작버튼 비활성화
                                    binding.deliveryStart.visibility =
                                        if (chatRoomData.participatePeopleId.size == chatRoomData.orderAcceptNum) View.VISIBLE else View.GONE
                                }
                                intent.putExtra("chatRoomData", chatRoomData)
                            }

                            //매칭방이 변경될때 리스너
                            MatchDao.matchRoomListener(matchId, {
                                //매칭방이 사라졌을때
                                //매칭방이 사라졌다는 메세지
                                Toast.makeText(context, "매칭방이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                outSettingChatRoom(false)
                            }) {
                                //배달이 시작됬을때
                                if (it.id == "start" && (intent.getSerializableExtra("matchRoomData") as MatchRoomData).id != "start") {
                                    deliveryStart()
                                }
                                //매칭방 데이터가 변경됬을때
                                intent.putExtra("matchRoomData", it)
                            }

                            //채팅창 보이게
                            binding.nochat.visibility = View.GONE

                            callback()
                        }
                    }
                }
                //매칭방이 없다면 나가기 액션
                else {
                    outSettingChatRoom(isExists)
                }
            }
        }
        //입장중이지 않았다면
        else {
            binding.nochat.visibility = View.VISIBLE
        }
    }

    fun updateSubTitle(
        orderAcceptNum: String = "",
        participatePeopleSize: String = "",
    ) {
        var acceptNum = orderAcceptNum
        var count = participatePeopleSize
        if (binding.toolbar.subtitle == null) {
            binding.toolbar.subtitle = ""
        }
        if (acceptNum == "" || count == "") {
            val regex = "\\d+".toRegex()
            val numbers =
                regex.findAll(binding.toolbar.subtitle.toString()).map { it.value.toInt() }.toList()

            //주문 수락자 명수만 온경우
            if (acceptNum != "") {
                count = numbers[1].toString()
            }
            //전체 명수만 온경우
            else if (count != "") {
                acceptNum = numbers[0].toString()
            }
            //아무것도 안온경우
            else {
                binding.toolbar.subtitle = ""
                return
            }
        }
        binding.toolbar.subtitle = String.format("%s / %s 주문 수락", acceptNum, count)
    }

    fun addNewMessageListener(matchId: String, callback: () -> Unit) {
        MatchDao.fetchNewMessage(matchId) {
            //내 채팅이라면
            if (it.userId == (intent.getSerializableExtra("user") as User).userId) {
                createMyChat(it)
                //무조건 스크롤 아래로 내리기
                binding.scrollView.post {
                    val bottom =
                        binding.scrollView.getChildAt(0).bottom + binding.scrollView.paddingBottom
                    binding.scrollView.scrollTo(0, bottom)
                }
            }
            //내채팅이 아니라면
            else {
                //현재 스크롤 최하단 위치
                val scrollMax =
                    (binding.scrollView.getChildAt(0).height - binding.scrollView.height).coerceAtLeast(
                        0
                    )
                //공지 사항이라면
                if (it.userId == "주문 수락" || it.userId == "주문 취소") {
                    createOrerAcceptText(it)
                } else if (it.userId == "입장" || it.userId == "퇴장") {
                    createEnterExitChat(it)
                }
                // 상대방 채팅이라면
                else {
                    createNotMyChat(it)
                }
                chatAnimation(scrollMax, it)
            }
            callback()
        }
    }

    private fun createEnterExitChat(it: Chat) {
        //바인딩 생성
        var acceptText = LayoutEnterExitChatBinding.inflate(layoutInflater)
        var color: Int? =
            resources.getColor(if (it.userId == "입장") R.color.white else R.color.red)

        acceptText.userName.text = it.userName
        acceptText.userId.text = it.userId
        acceptText.chat.text = it.chat

        acceptText.userName.setTextColor(color!!)
        acceptText.chat.setTextColor(color)

        acceptText.chatTime.text =
            DeliverTime(
                Calendar.getInstance().apply { timeInMillis = it.chatTime }).getTime()

        binding.chatLayout.addView(acceptText.root)
    }

    private fun createMyChat(it: Chat) {
        var infalte = LayoutMychatBinding.inflate(layoutInflater)
        infalte.userId.text = it.userId
        infalte.userName.text = it.userName
        infalte.chat.text = it.chat
        infalte.chatTime.text =
            DeliverTime(Calendar.getInstance().apply { timeInMillis = it.chatTime }).getTime()
        binding.chatLayout.addView(infalte.root)
    }

    private fun chatAnimation(scrollMax: Int, it: Chat) {
        binding.scrollView.post {
            //스크롤이 최하단이라면 최하단으로 움직인다.
            if (scrollMax == binding.scrollView.scrollY) {
                val bottom =
                    binding.scrollView.getChildAt(0).bottom + binding.scrollView.paddingBottom
                binding.scrollView.scrollTo(0, bottom)
            }
            //최하단보다  스크롤 하단 버튼을 활성화한다.
            else {
                Snackbar.make(
                    binding.chatLayout, "${it.userName}\n${it.chat}", Snackbar.LENGTH_LONG
                ).apply {
                    val textView =
                        view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                    setAction("아래로", { binding.scrollDonwButton.performClick() })
                    view.background = resources.getDrawable(R.drawable.custom_button_done)
                    anchorView = binding.input
                    textView.textSize = 18f
                }.show()
                binding.scrollDonwButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun createOrerAcceptText(it: Chat) {
        //바인딩 생성
        var acceptText = LayoutOrderacceptBinding.inflate(layoutInflater)
        var color: Int? =
            resources.getColor(if (it.userId == "주문 수락") R.color.orangeClick else R.color.orange)

        acceptText.userName.text = it.userName
        acceptText.userId.text = it.userId
        acceptText.chat.text = it.chat

        acceptText.userName.setTextColor(color!!)
        acceptText.chat.setTextColor(color)

        acceptText.chatTime.text =
            DeliverTime(Calendar.getInstance().apply { timeInMillis = it.chatTime }).getTime()

        binding.chatLayout.addView(acceptText.root)
    }

    private fun createDownScrollButtonListener() {
        binding.scrollDonwButton.visibility = View.INVISIBLE
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val bottom =
                binding.scrollView.getChildAt(0).bottom <= binding.scrollView.scrollY + binding.scrollView.height

            binding.scrollDonwButton.visibility = if (!bottom) View.VISIBLE else View.INVISIBLE

        }

        binding.scrollDonwButton.setOnClickListener {
            binding.scrollView.post { binding.scrollView.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun sendButtonSetting(edit: EditText, btn: Button) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 호출됩니다.
            }

            override fun afterTextChanged(s: Editable) {
                // 텍스트가 변경된 후에 호출됩니다.
                // 이 시점에서 EditText에 있는 텍스트를 확인합니다.
                btn.visibility = if (s.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })


    }

    private fun createSendListener() {

        binding.send.setOnClickListener {

            //!!!내가 친 채팅 DB보내기
            val chat = Chat(
                (intent.getSerializableExtra("user") as User).userId,
                (intent.getSerializableExtra("user") as User).userName,
                binding.input.text.toString(),
                Calendar.getInstance().timeInMillis
            )
            binding.input.text.clear()

            MatchDao.sendMessageToFirebase(chat, matchId) {}

        }
    }

    private fun createNotMyChat(chat: Chat) {

        val inflate = LayoutChatBinding.inflate(layoutInflater)

        inflate.userId.text = chat.userId
        inflate.userName.text = chat.userName
        inflate.chat.text = chat.chat
        inflate.chatTime.text =
            DeliverTime(Calendar.getInstance().apply { timeInMillis = chat.chatTime }).getTime()

        if (binding.chatLayout.childCount > 1) {
            val lastView =
                binding.chatLayout.getChildAt(binding.chatLayout.childCount - 1) as ViewGroup
            //마지막이 올리려는 채팅과 이름이 같다면 프로필제거
            if (lastView.findViewById<TextView>(R.id.userName).text.toString() == chat.userName) {
                inflate.profile.visibility = View.GONE
            }
        }


        binding.chatLayout.addView(inflate.root)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        //매칭방 입장 함수
        return binding.root
    }
}