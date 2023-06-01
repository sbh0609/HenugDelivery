package com.tuk.shdelivery.FragMent

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.tuk.shdelivery.Activity.ChargeActivity
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.FragmentMypageBinding
import com.tuk.shdelivery.Activity.settingActivity


class MypageFragment : Fragment() {
    val binding by lazy { FragmentMypageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //설정 아이콘 설정
        setting()

        //충전 버튼 설정
        binding.charge.setOnClickListener {
            var intent = Intent(activity, ChargeActivity::class.java)
            startActivity(intent)
            ToastCustom.toast(requireActivity(), "충전 액티비티 출력")
        }
    }

    private fun setting() {
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.settingIcon -> {
                    var intent = Intent(activity, settingActivity::class.java)
                    startActivity(intent)
                    ToastCustom.toast(requireActivity(), "설정창 액티비티 출력")
                }
            }
            true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}