package com.tuk.shdelivery.Data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class MatchDao {
    private val database = FirebaseDatabase.getInstance().reference

    private var childEventListener: ChildEventListener? = null

    /**
     * input: room <Room 객체>(데이터 클래스의 room객체를 사용한다)
     * output: void
     * 새로운 채팅방을 생성하고 데이터베이스에 저장한다.
     */
    fun createMatchingroom(user: User, match: MatchRoomData, callback: () -> Unit) {
        // Generate a new chatroom ID
        // Save the chatroom to the database
        database.child("chatrooms").child(match.id).setValue(match)
            .addOnSuccessListener {
                createChatRoom(ChatRoom(), match) {
                    callback()
                }
                Log.d("HandleData", "Chatroom created")
            }
            .addOnFailureListener {
                // An error occurred
                Log.d("HandleData", "Failed to create chatroom")
            }
    }

    fun joinUserMatchRoom(user: User, match: MatchRoomData, callback: () -> Unit) {
        //count값 증가
        database.child("chatrooms/${match.id}/count").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue == null) {
                    mutableData.value = 1
                } else {
                    mutableData.value = currentValue + 1
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?,
            ) {
            }
        })
        //participatePeopleId 리스트 요소 추가
        val reference = database.child("chatrooms/${match.id}/chatRoom/participatePeopleId")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list =
                    dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                var updatedList = ArrayList<String>()
                if (list != null) {
                    updatedList = list as ArrayList
                }

                updatedList?.add(user.userId)  // newItem은 추가할 항목
                reference.setValue(updatedList)
                    .addOnSuccessListener {
                        callback()
                    }.addOnFailureListener {
                        Log.d("fail100", it.toString())
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    fun exitUser(user: User, callback: () -> Unit) {
        //count값 감소
        database.child("chatrooms/${user.participateMatchId}/count").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue == null) {
                    mutableData.value = 1
                } else {
                    mutableData.value = currentValue - 1
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?,
            ) { }
        })
        //participatePeopleId 리스트 요소 삭제
        val reference = database.child("chatrooms/${user.participateMatchId}/chatRoom/participatePeopleId")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list =
                    dataSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                val updatedList = list as ArrayList

                updatedList.remove(user.userId)
                reference
                    .setValue(updatedList)
                    .addOnSuccessListener {
                        callback()
                    }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun createChatRoom(chatRoom: ChatRoom, matchRoomData: MatchRoomData, callback: () -> Unit) {
        database.child("chatrooms/${matchRoomData.id}/chatRoom").setValue(chatRoom)
            .addOnSuccessListener {
                // Chatroom was created successfully
                callback()
            }
            .addOnFailureListener {
                // An error occurred
                Log.d("HandleData", "Failed to create chatroom")
            }
    }

    /**
     * input: message <Message 객체>, chatroomId <String>
     *     사용자가 입력한 메시지, chatroomId는 카카오톡 고유 ID로 대체가능
     * output: void
     * 입력한 메시지를 데이터베이스에 저장한다.
     */
    fun sendMessageToFirebase(message: Chat, chatroomId: String) {
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
    fun fetchMessages(chatroomId: String, callback: (List<Chat>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val loadedMessages = mutableListOf<Chat>()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(Chat::class.java)
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
    fun fetchNewMessage(chatroomId: String, callback: (Chat) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Chat::class.java)
                if (message != null) {
                    callback(message)
                }
            }

            // 다른 메소드들은 필요에 따라 구현...
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        messagesRef.addChildEventListener(childEventListener!!)
    }

    fun removeMessageListener(chatroomId: String) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        childEventListener?.let {
            messagesRef.removeEventListener(it)
        }
    }

    /**
     * input: callback <(List<Chatroom>) -> Unit>
     * output: void
     * 모든 채팅방을 불러온다.
     * 채팅방 리스트는 callback 함수를 통해 반환된다.
     */
    fun fetchChatrooms(callback: (List<MatchRoomData>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val chatroomsRef = database.getReference("chatrooms")

        chatroomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val loadedChatrooms = mutableListOf<MatchRoomData>()
                for (snapshot in dataSnapshot.children) {
                    val chatroom = snapshot.getValue(MatchRoomData::class.java)
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
    

    fun getParticipatingMatch(user: User, callback: (match: MatchRoomData) -> Unit) {
        val matchroomRef =
            FirebaseDatabase.getInstance().getReference("chatrooms/${user.participateMatchId}")
        matchroomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.getValue(MatchRoomData::class.java)
                    Log.d("test1000", data.toString())
                    callback(data!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadData:onCancelled", databaseError.toException())
            }
        })
    }
    fun getChatRoomData(user: User,callback: (chatRoom : ChatRoom) -> Unit){
        val matchroomRef =
            FirebaseDatabase.getInstance().getReference("chatrooms/${user.participateMatchId}/chatRoom")
        matchroomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.getValue(ChatRoom::class.java)
                    callback(data!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadData:onCancelled", databaseError.toException())
            }
        })
    }

    fun removeMatchRoom(user: User, callback: () -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("chatrooms/${user.participateMatchId}")

        myRef.removeValue().addOnSuccessListener {
            callback()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to remove node", e)
        }
    }
}