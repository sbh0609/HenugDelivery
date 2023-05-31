package com.tuk.shdelivery

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HandleData{
    private val database = FirebaseDatabase.getInstance().reference

    fun createChatroom(room: Room) {
        // Generate a new chatroom ID
        val chatroomId = database.child("chatrooms").push().key

        chatroomId?.let {
            // Save the chatroom to the database
            database.child("chatrooms").child(it).setValue(room)
                .addOnSuccessListener {
                    // Chatroom was created successfully
                    Log.d("HandleData", "Chatroom created")
                }
                .addOnFailureListener {
                    // An error occurred
                    Log.d("HandleData", "Failed to create chatroom")
                }
        }
    }

    fun sendMessageToFirebase(message: Message, chatroomId: String) {
        database.child("chatrooms").child(chatroomId).child("messages").push().setValue(message)
    }

    fun fetchMessages(chatroomId: String, callback: (MutableList<Message>) -> Unit) {
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
                callback(messages)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log error
            }
        })
    }
}
