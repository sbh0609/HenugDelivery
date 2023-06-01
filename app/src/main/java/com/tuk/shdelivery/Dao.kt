package com.tuk.shdelivery

import android.util.Log
import com.google.firebase.database.*

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

    /**
     * fetchMessage는 채팅방에 입장할 때 지금까지의 채팅 내역을 전부 볼 수 있게 하는 코드이다.
     * 프론트엔트 개발자는 이 함수를 채팅방 입장 버튼을 누르면 구현되게 하면된다.
     *
     * 사용자가 메시지를 입력하면 sendMessageToFirebase에 저장된다.
     * 메시지 내역만 저장하면 되는 것이고 입력받은 메시지는 그냥 화면에 추가하면 된다.
     */
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
    fun fetchNewMessage(chatroomId: String, callback: (Message) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    callback(message)
                }
            }
            // 이외의 메서드들은 필요에 따라 구현하세요.
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) { }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) { }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) { }
            override fun onCancelled(databaseError: DatabaseError) { }

            // 다른 메소드들은 필요에 따라 구현...
        })
    }
}
