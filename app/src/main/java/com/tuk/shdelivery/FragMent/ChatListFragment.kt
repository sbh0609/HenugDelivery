package com.tuk.shdelivery.FragMent

import android.os.Bundle
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
    var initChat = false
    val matchDao = MatchDao()
    lateinit var matchId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    private fun orderAceptButtonListener() {
        binding.orderAccept.setOnClickListener {
            binding.orderAccept.isEnabled = false

            var text = ""

            //!!! 주문 수락 기능
            if (binding.orderAccept.text.toString() == "주문 수락") {

                //보유포인트보다 많이 입력하면 토스트 띄우기
                val userPoint = (intent.getSerializableExtra("user") as User).userPoint
                if (userPoint < binding.inputPoint.text.toString().toInt() || userPoint == 0L) {
                    Toast.makeText(requireContext(), "보유 포인트보다 많이 적을 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                    binding.orderAccept.isEnabled = true
                    return@setOnClickListener
                }
                binding.orderAccept.text = "주문 취소"
                text = "주문 수락"
                binding.inputPoint.isEnabled = false
                //accpet인원 올리기
                binding.orderAcceptNum.text =
                    (binding.orderAcceptNum.text.toString().toInt() + 1).toString()

            } else {
                binding.orderAccept.text = "주문 수락"
                text = "주문 취소"

                binding.inputPoint.isEnabled = true

                //accpet인원 내리기
                binding.orderAcceptNum.text =
                    (binding.orderAcceptNum.text.toString().toInt() - 1).toString()

            }
            //주문 수락|취소 Chat 만들기
            val chat = Chat(
                text,
                (intent.getSerializableExtra("user") as User).userName,
                binding.inputPoint.text.toString(),
                Calendar.getInstance().timeInMillis
            )

            matchDao.sendMessageToFirebase(
                chat,
                (intent.getSerializableExtra("user") as User).participateMatchId
            )
        }
    }

    private fun createExitButtonListener() {
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.exit -> {
                    exitChatRoom()
                }
            }
            true
        }
    }

    public fun exitChatRoom() {
        val user = intent.getSerializableExtra("user") as User
        //매칭방에 입장중인 상태에서만
        if (user.participateMatchId != "") {
            //본인이 만든방이 아니라면
            if (user.userId != user.participateMatchId) {
                matchDao.removeMessageListener(user.participateMatchId)
                user.participateMatchId = ""
                intent.putExtra("user", user)
                UserDao().updateUser(user) {
                    binding.clearChat.performClick()
                    binding.view.performClick()
                    updateSubTitle("", "")
                }
                initChat = false
            }
            //본인이 만든 방이면
            else {
                Toast.makeText(
                    activity,
                    "방을 생성한 사람이 나갈 수 없습니다.(방을 삭제해 주세요)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }//매칭방에 입장중이 아니면
        else if (user.participateMatchId == "") {
            val snackbar = Snackbar.make(
                binding.root,
                "매칭방을 찾아보세요!",
                Snackbar.LENGTH_SHORT
            )
            snackbar.setAction("알겠습니다.") {
                snackbar.dismiss()
            }.show()
            (activity as HomeActivity).binding.tabLayout.getTabAt(0)!!.select()
        }
    }

    public fun enterChatRoom(callback: () -> Unit) {
        //채팅방에 접속중인 사람이면 생성시 리스너 달기
        matchId = (intent.getSerializableExtra("user") as User).participateMatchId
        if (matchId != "") {
            //소제목으로 띄우기
            updateSubTitle()

            addNewMessageListener(matchId)
            fetchAllMessages(matchId) {
                //새로운 메세지 올때 리스너 등록
                callback()
                initChat = true
            }

            //채팅창 보이게
            binding.nochat.visibility = View.GONE
        } else {
            binding.nochat.visibility = View.VISIBLE
        }
    }

    fun updateSubTitle(
        orderAcceptNum: String = binding.orderAcceptNum.text.toString(),
        participatePeopleSize: String = binding.participatePeopleSize.text.toString(),
    ) {
        if (orderAcceptNum == "") {
            binding.toolbar.subtitle = ""
        } else {
            binding.toolbar.subtitle =
                String.format("%s / %s 주문 수락", orderAcceptNum, participatePeopleSize)
        }
    }

    fun addNewMessageListener(matchId: String) {
        matchDao.fetchNewMessage(matchId) {
            Log.d("test100", isAdded.toString())
            //처음이 아니고 리스너 역할
            if (initChat) {
                //공지 사항이라면
                if (it.userId == "주문 수락" || it.userId == "주문 취소") {
                    createOrerAcceptText(it)
                    //주문 수락,취소 버튼 활성화
                    if (it.userName == (intent.getSerializableExtra("user") as User).userName) {
                        binding.orderAccept.isEnabled = true
                    }
                }
                //채팅이라면
                else {
                    //다른 사람이 친 채팅이면 띄운다
                    if (it.userId != (intent.getSerializableExtra("user") as User).userId) {
                        //현재 스크롤 최하단 위치
                        val scrollMax =
                            (binding.scrollView.getChildAt(0).height - binding.scrollView.height).coerceAtLeast(
                                0
                            )
                        createNotMyChat(it)
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
                                    "${it.userName} 님이 보내셨습니다.\n${it.chat}",
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
                    //내 채팅은 안 띄운다
                }
            }
        }
    }

    private fun createOrerAcceptText(it: Chat) {
        //바인딩 생성
        var acceptText = LayoutOrderacceptBinding.inflate(layoutInflater)
        var color: Int? = null

        if (it.userId == "주문 수락") {
            acceptText.chat.text = "님이 ${it.chat}P 만큼 주문수락 하였습니다."
            color = resources.getColor(R.color.orangeClick)
        } else if (it.userId == "주문 취소") {
            acceptText.chat.text = "님이 취소 하였습니다."
            color = resources.getColor(R.color.orange)
        }

        acceptText.userName.text = it.userName
        acceptText.userId.text = it.userId

        acceptText.userName.setTextColor(color!!)
        acceptText.chat.setTextColor(color)

        acceptText.chatTime.text =
            DeliverTime(Calendar.getInstance().apply { timeInMillis = it.chatTime }).getTime()

        binding.chatLayout.addView(acceptText.root)
    }

    fun fetchAllMessages(matchId: String, callback: ()->Unit) {
        matchDao.fetchMessages(matchId) {
            for (i in it) {
                //공지 사항이라면
                if (i.userId == "주문 수락" || i.userId == "주문 취소") {
                    //바인딩 생성
                    createOrerAcceptText(i)
                }
                //채팅이면
                else {
                    //내채팅이면 그냥 띄우기
                    if (i.userId == (intent.getSerializableExtra("user") as User).userId) {
                        var infalte = LayoutMychatBinding.inflate(layoutInflater)
                        infalte.userId.text = i.userId
                        infalte.userName.text = i.userName
                        infalte.chat.text = i.chat
                        infalte.chatTime.text = DeliverTime(
                            Calendar.getInstance().apply { timeInMillis = i.chatTime }).getTime()
                        binding.chatLayout.addView(infalte.root)
                    }
                    //다른사람이 쓴 채팅이면
                    else {
                        createNotMyChat(i)
                    }
                }
            }
            callback()
        }
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
            //!!!내가 친 채팅 보내기 기능
            val mychat = LayoutMychatBinding.inflate(layoutInflater)
            mychat.chatTime.text = DeliverTime(Calendar.getInstance()).getTime()
            val chat = Chat(
                (intent.getSerializableExtra("user") as User).userId,
                (intent.getSerializableExtra("user") as User).userName,
                binding.input.text.toString(),
                Calendar.getInstance().timeInMillis
            )
            matchDao.sendMessageToFirebase(chat, matchId)


            mychat.chat.text = binding.input.text.toString()
            binding.input.text.clear()

            binding.chatLayout.addView(mychat.root)
            binding.scrollView.post {
                val bottom =
                    binding.scrollView.getChildAt(0).bottom + binding.scrollView.paddingBottom
                binding.scrollView.scrollTo(0, bottom)
            }

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
        enterChatRoom(){}
        return binding.root
    }
}