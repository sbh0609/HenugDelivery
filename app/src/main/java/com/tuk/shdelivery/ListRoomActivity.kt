package com.tuk.shdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.tuk.shdelivery.databinding.ActivityListRoomBinding

class ListRoomActivity : AppCompatActivity() {
    //클래스 맴버 변수 선언
    private lateinit var binding: ActivityListRoomBinding
    private lateinit var database: DatabaseReference
    private var chatrooms = mutableListOf<Chatroom>()

    //onCreate override
    override fun onCreate(savedInstanceState: Bundle?) {
        //뷰 바인딩 초기화
        super.onCreate(savedInstanceState)
        binding = ActivityListRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 데이터베이스의 reference datbase에 가져옴
        //FirebaseDatabase.getInstance().reference는 데이터베이스의 위치를 나타낸다.
        database = FirebaseDatabase.getInstance().reference
        //this는 현재 액티비티 클래스, 선형 레아이웃 매니저 설정
        // LayoutManager은 recyclerview에 항목이 어떻게 배치될지 결정
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatrooms.clear()
                for (chatroomSnapshot in dataSnapshot.children) {
                    val chatroom = chatroomSnapshot.getValue(Chatroom::class.java)?.apply {
                        id = chatroomSnapshot.key ?: ""
                    }
                    if (chatroom != null) {
                        chatrooms.add(chatroom)
                    }
                }

                // Set the adapter
                binding.recyclerview.adapter = ChatroomAdapter(chatrooms)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting data failed, log a message
                // ...
            }
        }
        database.child("chatrooms").addValueEventListener(postListener)
    }
}
