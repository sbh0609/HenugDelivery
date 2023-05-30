package com.tuk.shdelivery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.tuk.shdelivery.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatroomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatroomId = intent.getStringExtra("chatroomId") ?: ""

        binding.btnSend.setOnClickListener {
            val message = Message(binding.etMessage.text.toString())
            sendMessageToFirebase(message, chatroomId)
            binding.etMessage.text.clear()
        }

        fetchMessages()
    }

    private fun sendMessageToFirebase(message: Message, chatroomId: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("chatrooms").child(chatroomId).child("messages").push().setValue(message)
    }

    private fun fetchMessages() {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (messageSnapshot in dataSnapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                binding.rvMessages.layoutManager = LinearLayoutManager(this@ChatActivity)
                binding.rvMessages.adapter = ChatAdapter(messages)
                // Scroll to bottom when new message added
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log error
            }
        })
    }
}

data class Message(var content: String="")



