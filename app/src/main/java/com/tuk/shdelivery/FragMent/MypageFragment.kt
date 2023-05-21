package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.tuk.shdelivery.HomeActivity
import com.tuk.shdelivery.R
import com.tuk.shdelivery.databinding.FragmentHomeBinding
import com.tuk.shdelivery.databinding.FragmentMypageBinding


class MypageFragment : Fragment() {
    val binding by lazy { FragmentMypageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener { item:MenuItem ->
            when(item.itemId){
                R.id.settingIcon->{
                    Toast.makeText(activity,"설정창 액티비티 출력",Toast.LENGTH_SHORT).show()
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