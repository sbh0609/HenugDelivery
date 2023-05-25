package com.tuk.shdelivery.FragMent

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.FragmentMypageBinding
import com.tuk.shdelivery.Activity.settingActivity


class MypageFragment : Fragment() {
    val binding by lazy { FragmentMypageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener { item:MenuItem ->
            when(item.itemId){
                R.id.settingIcon->{
                    var intent = Intent(activity, settingActivity::class.java)
                    startActivity(intent)
                    ToastCustom.toast(requireActivity(),"설정창 액티비티 출력")
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