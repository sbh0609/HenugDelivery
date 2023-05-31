package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.databinding.FragmentChatListBinding
import com.tuk.shdelivery.databinding.LayoutChatBinding
import com.tuk.shdelivery.databinding.LayoutMychatBinding
import org.w3c.dom.Text
import java.util.Calendar


class ChatListFragment : Fragment() {

    val binding by lazy { FragmentChatListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //변경해야함
        binding.btn.setOnClickListener {
            if (binding.nochat.visibility == View.GONE) {
                binding.nochat.visibility = View.VISIBLE
            } else if (binding.nochat.visibility == View.VISIBLE) {
                binding.nochat.visibility = View.GONE
            }
        }

        //내챗아님 채팅 올림
        binding.test.setOnClickListener {
            createNotMyChat()
        }

        //보내기 버튼 활성, 비활성화
        sendButtonSetting()

        //메세지 보내기 리스너
        createSendListener()

        //다운 스크롤 버튼 리스너
        createDownScrollButtonListener()
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

    private fun sendButtonSetting() {
        binding.input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 호출됩니다.
            }

            override fun afterTextChanged(s: Editable) {
                // 텍스트가 변경된 후에 호출됩니다.
                // 이 시점에서 EditText에 있는 텍스트를 확인합니다.
                binding.send.visibility = if (s.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    private fun createSendListener() {
        binding.send.setOnClickListener {
            val mychat = LayoutMychatBinding.inflate(layoutInflater)
            Log.d("String", binding.input.text.toString())
            mychat.text.text = binding.input.text.toString()
            mychat.chatTime.text = DeliverTime(Calendar.getInstance()).getTime()
            binding.input.text.clear()

            binding.chatLayout.addView(mychat.root)
            binding.scrollView.post {
                val bottom =
                    binding.scrollView.getChildAt(0).bottom + binding.scrollView.paddingBottom
                binding.scrollView.scrollTo(0, bottom)
            }

        }
    }


    private fun createNotMyChat() {
        val inflate = LayoutChatBinding.inflate(layoutInflater)

        if (binding.chatLayout.childCount > 1) {
            val lastView =
                binding.chatLayout.getChildAt(binding.chatLayout.childCount - 1) as ViewGroup
            //마지막이 올리려는 채팅과 이름이 같다면 프로필제거
            if (lastView.findViewById<TextView>(R.id.name).text == inflate.name.text) {
                inflate.profile.visibility = View.GONE
            }
        }
        //현재 스크롤 최하단 위치
        val scrollMax =
            (binding.scrollView.getChildAt(0).height - binding.scrollView.height).coerceAtLeast(0)

        binding.chatLayout.addView(inflate.root)
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
                    "${inflate.name.text} 님이 보내셨습니다.\n${inflate.text.text.toString()}",
                    Snackbar.LENGTH_LONG
                ).apply {
                    val textView =
                        view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                    setAction("아래로",{binding.scrollDonwButton.performClick()})
                    view.background = resources.getDrawable(R.drawable.custom_button_done)
                    anchorView = binding.input
                    textView.textSize = 18f
                }.show()
                binding.scrollDonwButton.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        Log.d("fragment", "onView")
        return binding.root
    }
}