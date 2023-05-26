package com.tuk.shdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.tuk.shdelivery.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ChatAdapter
    private var messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(messages)
        binding.rvMessages.adapter = adapter

        val postListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val message = dataSnapshot.getValue(Message::class.java)
                if (message != null) {
                    messages.add(message)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) { /* ... */ }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) { /* ... */ }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) { /* ... */ }
            override fun onCancelled(databaseError: DatabaseError) { /* ... */ }
        }
        database.child("messages").addChildEventListener(postListener)

        binding.btnSend.setOnClickListener {
            val content = binding.etMessage.text.toString()
            if (content.isNotBlank()) {
                val message = Message(content) // 여기에 필요한 필드를 추가해주세요
                database.child("messages").push().setValue(message)
                binding.etMessage.text.clear()
            }
        }
    }
}

data class Message(var content: String="")

