package com.tuk.shdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.tuk.shdelivery.databinding.ActivityListRoomBinding

class ListRoomActivity : AppCompatActivity() {
    // 클래스 멤버 변수 선언
    private lateinit var binding: ActivityListRoomBinding
    private val handleData = HandleData()

    // onCreate override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        handleData.fetchChatrooms { loadedChatrooms ->
            // Set the adapter
            binding.recyclerview.adapter = ChatroomAdapter(loadedChatrooms, this@ListRoomActivity)
        }
    }
}
