package com.tuk.shdelivery.FragMent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.tuk.shdelivery.Activity.ChargeActivity
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.FragmentMypageBinding
import com.tuk.shdelivery.Activity.settingActivity
import com.tuk.shdelivery.Data.User


class MypageFragment : Fragment() {
    val intent by lazy { requireActivity().intent }
    val binding by lazy { FragmentMypageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //충전 버튼 설정
        binding.charge.setOnClickListener {
            intent.setClass(requireContext(),ChargeActivity::class.java)
            activity?.startActivityForResult(intent, 2)
        }
        //마이페이지 유저이름, 포인트 설정
        updateProfile()
    }

    public fun updateProfile() {
        val user = intent.getSerializableExtra("user") as User
        binding.userName.text = user.userName
        binding.point.text = user.userPoint.toString() + "P"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}