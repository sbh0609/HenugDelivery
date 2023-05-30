package com.tuk.shdelivery

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.tuk.shdelivery.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatroomId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        askForUserId() // 사용자 ID를 요청합니다.

        chatroomId = intent.getStringExtra("chatroomId") ?: ""

        binding.btnSend.setOnClickListener {
            val message =
                Message(binding.etMessage.text.toString(), userId, System.currentTimeMillis())
            sendMessageToFirebase(message, chatroomId)
            binding.etMessage.text.clear()
        }

//        fetchMessages()
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
                binding.rvMessages.adapter = MessageAdapter(messages, userId) // Add userId here
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log error
            }
        })
    }

    private fun askForUserId() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Enter your user ID:")

        val inputField = EditText(this)
        dialogBuilder.setView(inputField)

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            userId = inputField.text.toString()
            dialog.dismiss()

            // Call fetchMessages() here after userId is set.
            fetchMessages()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

}
data class Message(var content: String="", var userId: String="", var timestamp: Long=0)



