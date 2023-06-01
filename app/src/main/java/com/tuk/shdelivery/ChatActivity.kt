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
    private val handleData = HandleData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askForUserId()

        chatroomId = intent.getStringExtra("chatroomId") ?: ""

        binding.btnSend.setOnClickListener {
            val messageContent = binding.etMessage.text.toString()
            val message =
                Message(binding.etMessage.text.toString(), userId, System.currentTimeMillis())
            handleData.sendMessageToFirebase(message, chatroomId)
            binding.etMessage.text.clear()
        }
    }
    private fun askForUserId() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Enter your user ID:")

        val inputField = EditText(this)
        dialogBuilder.setView(inputField)

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            userId = inputField.text.toString()
            dialog.dismiss()

            val messages = mutableListOf<Message>()
            val adapter = MessageAdapter(messages, userId)
            binding.rvMessages.layoutManager = LinearLayoutManager(this@ChatActivity)
            binding.rvMessages.adapter = adapter

            handleData.fetchMessages(chatroomId) { loadedMessages ->
                messages.addAll(loadedMessages)
                adapter.notifyDataSetChanged()
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }

            handleData.fetchNewMessage(chatroomId) { newMessage ->
                messages.add(newMessage)
                adapter.notifyItemInserted(messages.size - 1)
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}