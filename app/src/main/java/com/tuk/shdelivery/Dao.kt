package com.tuk.shdelivery

import android.util.Log
import com.google.firebase.database.*

class HandleData{
    private val database = FirebaseDatabase.getInstance().reference
    /**
     * input: room <Room 객체>(데이터 클래스의 room객체를 사용한다)
     * output: void
     * 새로운 채팅방을 생성하고 데이터베이스에 저장한다.
     */
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

    /**
     * input: message <Message 객체>, chatroomId <String>
     *     사용자가 입력한 메시지, chatroomId는 카카오톡 고유 ID로 대체가능
     * output: void
     * 입력한 메시지를 데이터베이스에 저장한다.
     */
    fun sendMessageToFirebase(message: Message, chatroomId: String) {
        database.child("chatrooms").child(chatroomId).child("messages").push().setValue(message)
    }
    /**
     * input: chatroomId <String>, callback <(List<Message>) -> Unit> 마찬가지로
     * 마찬가지로 chatroomId는 카카오톡 Id로 대체 메시지는 전체 메시지
     * output: void
     * 해당 채팅방의 모든 메시지를 불러온다.(채팅방 입장 시)
     * 메시지는 callback 함수를 통해 반환된다.
     */

    /**
     * fetchMessage는 채팅방에 입장할 때 지금까지의 채팅 내역을 전부 볼 수 있게 하는 코드이다.
     * 프론트엔트 개발자는 이 함수를 채팅방 입장 버튼을 누르면 구현되게 하면된다.
     *
     * 사용자가 메시지를 입력하면 sendMessageToFirebase에 저장된다.
     * 메시지 내역만 저장하면 되는 것이고 입력받은 메시지는 그냥 화면에 추가하면 된다.
     */
    fun fetchMessages(chatroomId: String, callback: (List<Message>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val loadedMessages = mutableListOf<Message>()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null) {
                        loadedMessages.add(message)
                    }
                }
                callback(loadedMessages)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // DB에서 읽는데 실패했을 경우
                // 로그를 출력하거나 사용자에게 알림을 보냄
            }
        })
    }
    /**
     * input: chatroomId <String>, callback <(Message) -> Unit>
     * chatroomId는 카카오톡 Id로 대체
     * output: void
     * 해당 채팅방의 새로운 메시지를 실시간으로 불러온다.
     * 사용자가 입력한 메시지를 표시
     * 메시지는 callback 함수를 통해 반환된다.
     */
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
    /**
     * input: callback <(List<Chatroom>) -> Unit>
     * output: void
     * 모든 채팅방을 불러온다.
     * 채팅방 리스트는 callback 함수를 통해 반환된다.
     */
    fun fetchChatrooms(callback: (List<Chatroom>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val chatroomsRef = database.getReference("chatrooms")

        chatroomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val loadedChatrooms = mutableListOf<Chatroom>()
                for (snapshot in dataSnapshot.children) {
                    val chatroom = snapshot.getValue(Chatroom::class.java)
                    if (chatroom != null) {
                        chatroom.id = snapshot.key ?: ""
                        loadedChatrooms.add(chatroom)
                    }
                }
                callback(loadedChatrooms)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // DB에서 읽는데 실패했을 경우
                // 로그를 출력하거나 사용자에게 알림을 보냄
            }
        })
    }
}
