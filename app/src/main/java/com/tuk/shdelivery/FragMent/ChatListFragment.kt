package com.tuk.shdelivery.FragMent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tuk.shdelivery.R
import com.tuk.shdelivery.databinding.FragmentChatListBinding


class ChatListFragment : Fragment() {
    val binding by lazy { FragmentChatListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("fragment","oncreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("fragment","onView")
        return binding.root
    }
}