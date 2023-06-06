package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Visibility
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
import com.tuk.shdelivery.Data.Chat
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.R
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.databinding.FragmentChatListBinding
import com.tuk.shdelivery.databinding.LayoutChatBinding
import com.tuk.shdelivery.databinding.LayoutMychatBinding
import com.tuk.shdelivery.databinding.LayoutOrderacceptBinding
import java.util.*


class ChatListFragment : Fragment() {

    val intent by lazy { requireActivity().intent }
    val binding by lazy { FragmentChatListBinding.inflate(layoutInflater) }
    lateinit var matchId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterChatRoom() {}

        //view, clear 버튼 리스너
        visibleListener()

        //나가기 버튼 리스너
        createExitButtonListener()

        //주문 수락 버튼 리스너
        orderAceptButtonListener()

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

        }
    }

    fun allOrderAccept() {
        //모두 주문을 눌렀을때!!
        binding.orderAccept.visibility = View.GONE
        binding.inputPoint.isEnabled = false
        binding.deliveryComplite.visibility = View.VISIBLE
        Toast.makeText(context, "배달에 대해 말해 주세요!", Toast.LENGTH_SHORT).show()

        ((activity as HomeActivity).listFragment[2] as MypageFragment).SetProfile(
            (intent.getSerializableExtra(
                "user"
            ) as User)
        )
    }

    private fun orderAceptButtonListener() {
        binding.orderAccept.setOnClickListener {
            binding.orderAccept.isEnabled = false
            val regex = "\\d+".toRegex()
            val numbers =
                regex.findAll(binding.toolbar.subtitle.toString()).map { it.value.toInt() }.toList()

            var text = ""
            var point = 0

            //혼자 일땐..
            if (numbers[1] == 1) {
                Toast.makeText(context, "같이 배달먹어요!!", Toast.LENGTH_SHORT).show()
                binding.orderAccept.isEnabled = true
                return@setOnClickListener
            }
            val user = intent.getSerializableExtra("user") as User
            //!!! 주문 수락 눌렀을때
            if (binding.orderAccept.text.toString() == "주문 수락") {

                //보유포인트보다 많이 입력하면 토스트 띄우기
                val userPoint = user.userPoint
                if (userPoint < binding.inputPoint.text.toString().toInt() || userPoint == 0L) {
                    Toast.makeText(requireContext(), "보유 포인트보다 많이 적을 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                    binding.orderAccept.isEnabled = true
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
                "%sP 만큼 %s 하였습니다.",
                binding.inputPoint.text.toString(),
                text
            )
            //주문 수락|취소 Chat 만들기
            val chat = Chat(
                text,
                user.userName,
                orderChat,
                Calendar.getInstance().timeInMillis
            )

            MatchDao.sendMessageToFirebase(
                chat,
                user.participateMatchId
            )

            user.userPoint -= point
            user.matchPoint = if (point > 0) point.toLong() else 0L
            intent.putExtra("user", user)

            MatchDao.updateOrderPoint(user.participateMatchId, point) { }
            UserDao().updateUser(user) {
                binding.orderAccept.isEnabled = true
                ((activity as HomeActivity).listFragment[2] as MypageFragment).SetProfile(user)
            }
        }
    }

    private fun createExitButtonListener() {
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.exit -> {
                    val user = intent.getSerializableExtra("user") as User

                    // 방 주인인지 판단
                    val isOwner = user.userId == user.participateMatchId

                    // 매칭방에 참가 중인지 판단
                    val isParticipating = user.participateMatchId != ""

                    // 모든 사용자가 주문을 수락한 상태인지 판단
                    val isAllAccepted = if (isParticipating) {
                        val regex = "\\d+".toRegex()
                        val numbers =
                            regex.findAll(binding.toolbar.subtitle).map { it.value.toInt() }
                                .toList()
                        numbers[0] == numbers[1]
                    } else false

                    when {
                        // 모든 사용자가 주문을 수락한 상태라면
                        isAllAccepted -> {
                            Toast.makeText(
                                context,
                                "배달을 완료하세요!! \n문제발생시 문의사항으로 연락주세요",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // 방 주인이라면
                        isOwner -> {
                            Toast.makeText(
                                activity,
                                "방을 생성한 사람이 나갈 수 없습니다.(방을 삭제해 주세요)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // 매칭방에 참가 중이라면
                        isParticipating -> {
                            MatchDao.exitUser(user) {
                                outSettingChatRoom()
                            }
                        }
                        // 아무것도 해당하지 않는 경우
                        else -> {
                            val snackbar =
                                Snackbar.make(binding.root, "매칭방을 찾아보세요!", Snackbar.LENGTH_SHORT)
                            snackbar.setAction("알겠습니다.") { snackbar.dismiss() }.show()
                        }
                    }
                }
            }
            true
        }
    }

    public fun outSettingChatRoom() {
        val user = intent.getSerializableExtra("user") as User
        if (binding.orderAccept.text == "주문 취소") {
            binding.orderAccept.performClick()
        }

        binding.clearChat.performClick()
        binding.view.performClick()

        binding.inputPoint.text = null
        binding.inputPoint.isEnabled = true
        binding.orderAccept.visibility = View.GONE
        binding.orderAccept.isEnabled = true
        MatchDao.removeListener(user.participateMatchId)
        user.participateMatchId = ""
        intent.putExtra("user", user)
        UserDao().updateUser(user) {
            updateSubTitle()
            ((activity as HomeActivity).listFragment[0] as HomeFragment).reFresh()
            ((activity as HomeActivity).listFragment[2] as MypageFragment).SetProfile(user)
        }
    }

    public fun enterChatRoom(callback: () -> Unit) {
        //채팅방에 접속중인 사람이면 생성시 리스너 달기
        val user = intent.getSerializableExtra("user") as User
        matchId = user.participateMatchId
        if (matchId != "") {

            MatchDao.getChatRoomData(user) {
                //주문 수락 중이고, 배달 상태가 아니라면
                if (user.matchPoint != 0L && it.orderAcceptPeopleId.size != it.participatePeopleId.size) {
                    binding.inputPoint.setText(user.matchPoint.toString())
                    binding.inputPoint.isEnabled = false
                    binding.orderAccept.isEnabled = true
                    binding.orderAccept.text = "주문 취소"

                    //배달 상태라면
                    if (it.orderAcceptPeopleId.size == it.participatePeopleId.size) {
                        allOrderAccept()
                    }
                }

                //소제목으로 띄우기
                updateSubTitle(
                    it.orderAcceptPeopleId.size.toString(),
                    it.participatePeopleId.size.toString()
                )
                //모든 채팅을 불러오고 새로운 메세지 올때 리스너 등록
                addNewMessageListener(matchId) {
                }
                //count 리스너
                MatchDao.addPeopleNumListener(matchId) {
                    updateSubTitle(participatePeopleSize = it.toString())
                }

                //모두 주문 수락 리스너
                MatchDao.addOrderAcceptListener(matchId, { int ->
                    updateSubTitle(orderAcceptNum = int.toString())
                },
                    { allOrderAccept() })

                ((activity as HomeActivity).listFragment[2] as MypageFragment).SetProfile(user)

                //채팅창 보이게
                binding.nochat.visibility = View.GONE
            }
        } else {
            binding.nochat.visibility = View.VISIBLE
        }
        callback()
    }

    fun updateSubTitle(
        orderAcceptNum: String = "",
        participatePeopleSize: String = "",
    ) {

        var acceptNum = orderAcceptNum
        var count = participatePeopleSize

        if (acceptNum == "" || count == "") {
            val regex = "\\d+".toRegex()
            val numbers =
                regex.findAll(binding.toolbar.subtitle.toString()).map { it.value.toInt() }.toList()

            //주문 수락자 명수만 온경우
            if (acceptNum != "") {
                count = numbers[1].toString()
            }
            //전체 명수만 온경우
            else if (count != ""){
                acceptNum = numbers[0].toString()
            }
            //아무것도 안온경우
            else{
                binding.toolbar.subtitle = ""
                return
            }
        }
        binding.toolbar.subtitle =
            String.format("%s / %s 주문 수락", acceptNum, count)
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

    private fun createMyChat(it: Chat) {
        var infalte = LayoutMychatBinding.inflate(layoutInflater)
        infalte.userId.text = it.userId
        infalte.userName.text = it.userName
        infalte.chat.text = it.chat
        infalte.chatTime.text = DeliverTime(
            Calendar.getInstance().apply { timeInMillis = it.chatTime }).getTime()
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
                    binding.chatLayout,
                    "${it.userName}\n${it.chat}",
                    Snackbar.LENGTH_LONG
                ).apply {
                    val textView =
                        view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                    setAction("아래로", { binding.scrollDonwButton.performClick() })
                    view.background =
                        resources.getDrawable(R.drawable.custom_button_done)
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
        var color: Int? = null

        if (it.userId == "주문 수락") {
            color = resources.getColor(R.color.orangeClick)
        } else if (it.userId == "주문 취소") {
            color = resources.getColor(R.color.orange)
        }

        acceptText.userName.text = it.userName
        acceptText.userId.text = it.userId
        acceptText.chat.text = it.chat

        acceptText.userName.setTextColor(color!!)
        acceptText.chat.setTextColor(color)

        acceptText.chatTime.text =
            DeliverTime(Calendar.getInstance().apply { timeInMillis = it.chatTime }).getTime()

        binding.chatLayout.addView(acceptText.root)
    }

    private fun visibleListener() {
        binding.view.setOnClickListener {
            if (binding.nochat.visibility == View.GONE) {
                binding.nochat.visibility = View.VISIBLE
            } else if (binding.nochat.visibility == View.VISIBLE) {
                binding.nochat.visibility = View.GONE
            }
        }

        //채팅창 클리어 버튼 설정
        binding.clearChat.setOnClickListener {
            while (binding.chatLayout.childCount > 1) {
                binding.chatLayout.removeViewAt(1)
            }
        }
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

            MatchDao.sendMessageToFirebase(chat, matchId)

        }
    }

    private fun createNotMyChat(chat: Chat) {

        val inflate = LayoutChatBinding.inflate(layoutInflater)

        inflate.userId.text = chat.userId
        inflate.userName.text = chat.userName
        inflate.chat.text = chat.chat
        inflate.chatTime.text = DeliverTime(
            Calendar.getInstance().apply { timeInMillis = chat.chatTime }).getTime()

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