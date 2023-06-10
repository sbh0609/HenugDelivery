package com.tuk.shdelivery.Data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.*

object MatchDao {
    private var database = FirebaseDatabase.getInstance().reference

    var childEventListener: ChildEventListener? = null
    var orderAcceptListener: ValueEventListener? = null
    var deliveryCompliteListener: ValueEventListener? = null
    var matchRoomListener: ValueEventListener? = null
    var refHashMap = hashMapOf<String,Pair<DatabaseReference,ValueEventListener>>()

    //매칭방이 있는지 확인 하는 함수 (true, false)
    fun isMatchExists(matchId: String, callback: (isExists: Boolean) -> Unit) {
        val ref = database.child("chatrooms/${matchId}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ", error.toException())
            }
        })
    }

    fun deliveryStart(matchId: String, callback: () -> Unit) {
        // Generate a new chatroom ID
        // Save the chatroom to the database
        database.child("chatrooms/${matchId}/id").setValue("start")
            .addOnSuccessListener {
                callback()
                Log.d("HandleData", "Chatroom created")
            }
            .addOnFailureListener {
                // An error occurred
                Log.d("HandleData", "Failed to create chatroom")
            }
    }

    fun matchRoomListener(
        matchId: String,
        callback: () -> Unit,
        callback2: (matchRoomData: MatchRoomData) -> Unit,
    ) {
        matchRoomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // 노드가 삭제되었을 때 실행될 코드
                    callback()
                }
                //매칭방 데이터가 변경될때 실행될 코드
                else {
                    val value = snapshot.getValue(MatchRoomData::class.java)
                    if (value != null){
                        callback2(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        val ref = database.child("chatrooms/${matchId}")
        refHashMap["matchRoomListener"] = Pair(ref,matchRoomListener!!)
        ref.addValueEventListener(matchRoomListener!!)

    }

    fun deliveryCompliteListener(user: User, callback: () -> Unit) {
        val ref3 =
            database.child("chatrooms/${user.participateMatchId}/chatRoom/orderAcceptPeopleId")

        deliveryCompliteListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null) {
                    callback()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "loadData:onCancelled", databaseError.toException())
            }
        }
        refHashMap["deliveryCompliteListener"] = Pair(ref3,deliveryCompliteListener!!)
        ref3.addValueEventListener(deliveryCompliteListener!!)
    }

    fun orderUserPlus2(user: User, callback: (Any?) -> (Unit)) {
        val orderAcceptNumRef =
            database.child("chatrooms/${user.participateMatchId}/chatRoom/orderAcceptNum")

        orderAcceptNumRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Long::class.java)
                if (currentValue == null) {
                    mutableData.value = 1L
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
                // OrderAcceptNum 트랜잭션 완료 후, 다음 작업을 실행
                if (committed) {
                    //orderAcceptPeopleId 리스트 요소 추가
                    val ref3 =
                        database.child("chatrooms/${user.participateMatchId}/chatRoom/orderAcceptPeopleId")
                    ref3.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val list = mutableData.getValue(object :
                                GenericTypeIndicator<List<String>>() {})
                            val updatedList =
                                if (list != null) list as ArrayList else ArrayList<String>()

                            // Only update if user is not already in the list
                            if (!updatedList.contains(user.userId)) {
                                updatedList.add(user.userId)
                                mutableData.value = updatedList
                            }

                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            dataSnapshot: DataSnapshot?,
                        ) {
                            if (databaseError == null) {
                                callback(null)
                            } else {
                                callback(databaseError)
                            }
                        }
                    })
                }
            }
        })
    }

    fun orderUserMisnus2(user: User, callback: (Any?) -> (Unit)) {
        val orderAcceptNumRef =
            database.child("chatrooms/${user.participateMatchId}/chatRoom/orderAcceptNum")

        orderAcceptNumRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Long::class.java)
                if (currentValue == null || currentValue <= 0) {
                    mutableData.value = 0L
                } else {
                    mutableData.value = currentValue - 1
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?,
            ) {
                // OrderAcceptNum 트랜잭션 완료 후, 다음 작업을 실행
                if (committed) {
                    //orderAcceptPeopleId 리스트 요소 삭제
                    val ref3 =
                        database.child("chatrooms/${user.participateMatchId}/chatRoom/orderAcceptPeopleId")
                    ref3.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val list = mutableData.getValue(object :
                                GenericTypeIndicator<List<String>>() {})
                            val updatedList = list as ArrayList

                            // Only update if user is in the list
                            if (updatedList.contains(user.userId)) {
                                updatedList.remove(user.userId)
                                mutableData.value = updatedList
                            }

                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            dataSnapshot: DataSnapshot?,
                        ) {
                            if (databaseError == null) {
                                callback(null)
                            } else {
                                callback(databaseError)
                            }
                        }
                    })
                }
            }
        })
    }

    fun deliveryComplite(user: User, callback: (Any?) -> Unit) {
        //orderAcceptPeopleId 리스트 요소 삭제
        val ref3 =
            database.child("chatrooms/${user.participateMatchId}/chatRoom/orderAcceptPeopleId")
        ref3.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val list = mutableData.getValue(object : GenericTypeIndicator<List<String>>() {})
                val updatedList = list as ArrayList

                updatedList.remove(user.userId)
                mutableData.value = updatedList

                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?,
            ) {
                // Transaction completed
                if (committed) {
                    callback(null)
                } else {
                    callback(databaseError)
                }
            }
        })
    }

    fun updateOrderPoint(ownerId: String, point: Int, callback: () -> Unit) {

        val reference = database.child("chatrooms/${ownerId}/chatRoom/orderPoint")
        reference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Long::class.java)
                if (currentValue == null) {
                    mutableData.value = 0L  // 처음 설정하는 경우, 값을 10으로 설정
                } else {
                    mutableData.value = currentValue + point.toLong()  // 기존 값에 10을 더함
                }
                return Transaction.success(mutableData) // 트랜잭션의 결과를 반환
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?,
            ) {
                // 트랜잭션이 완료된 후 실행할 작업
                if (databaseError == null) {
                    callback()
                }
            }
        })
    }

    fun addOrderAcceptListener(
        matchId: String,
        callback: (chatRoom: ChatRoom) -> Unit,
    ) {
        val ref1 = database.child("chatrooms/${matchId}/chatRoom")

        orderAcceptListener = object : ValueEventListener {
            override fun onDataChange(snapshot1: DataSnapshot) {
                if (snapshot1.exists()) {
                    val chatRoom = snapshot1.getValue(ChatRoom::class.java)
                    if (chatRoom != null) {
                        callback(chatRoom)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        refHashMap["orderAcceptListener"] = Pair(ref1,orderAcceptListener!!)
        // ValueEventListener 추가
        ref1.addValueEventListener(orderAcceptListener!!)
    }

    fun removeListener(matchId: String) {
        for(i in refHashMap){
            i.value.first.removeEventListener(i.value.second)
        }
        removeMessageListener(matchId)
    }

    fun removeMessageListener(chatroomId: String) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("chatrooms/$chatroomId/messages")

        childEventListener?.let {
            messagesRef.removeEventListener(it)
        }
    }

    /**
     * input: room <Room 객체>(데이터 클래스의 room객체를 사용한다)
     * output: void
     * 새로운 채팅방을 생성하고 데이터베이스에 저장한다.
     */
    fun createMatchingRoom(user: User, match: MatchRoomData, callback: () -> Unit) {
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
// count값 증가
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
                if (databaseError != null) {
                    Log.e("Firebase", "Error in count transaction: ${databaseError.message}")
                } else if (committed) {
                    //participatePeopleId 리스트 요소 추가
                    val reference =
                        database.child("chatrooms/${match.id}/chatRoom/participatePeopleId")
                    reference.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val list = mutableData.getValue(object :
                                GenericTypeIndicator<List<String>>() {})
                            val updatedList =
                                if (list != null) list as ArrayList else ArrayList<String>()

                            updatedList.add(user.userId)  // newItem은 추가할 항목
                            mutableData.value = updatedList

                            return Transaction.success(mutableData)
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,
                            committed: Boolean,
                            dataSnapshot: DataSnapshot?,
                        ) {
                            if (databaseError != null) {
                                Log.e(
                                    "Firebase",
                                    "Error in participatePeopleId transaction: ${databaseError.message}"
                                )
                            } else if (committed) {
                                callback()
                            }
                        }
                    })
                }
            }
        })

    }

    fun exitUser(user: User, callback: () -> Unit) {
        database.child("chatrooms/${user.participateMatchId}/count")
            .runTransaction(object : Transaction.Handler {
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
                ) {
                    if (committed) {
                        // 'count' 트랜잭션 완료 후 'participatePeopleId' 작업 수행
                        val reference =
                            database.child("chatrooms/${user.participateMatchId}/chatRoom/participatePeopleId")
                        reference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val list = dataSnapshot.getValue(object :
                                    GenericTypeIndicator<List<String>>() {})
                                val updatedList = list as ArrayList<String>

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
                }
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

    /**
     * input: callback <(List<Chatroom>) -> Unit>
     * output: void
     * 모든 채팅방을 불러온다.
     * 채팅방 리스트는 callback 함수를 통해 반환된다.
     */
    fun fetchChatrooms(callback: (ArrayList<MatchRoomData>) -> Unit) {
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
                callback(loadedChatrooms as ArrayList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // DB에서 읽는데 실패했을 경우
                // 로그를 출력하거나 사용자에게 알림을 보냄
            }
        })
    }

    fun getParticipatingMatch(matchId: String, callback: (match: MatchRoomData?) -> Unit) {
        val matchroomRef =
            FirebaseDatabase.getInstance().getReference("chatrooms/${matchId}")
        matchroomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.getValue(MatchRoomData::class.java)
                    callback(data!!)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadData:onCancelled", databaseError.toException())
            }
        })
    }

    fun getChatRoomData(matchId: String, callback: (chatRoom: ChatRoom?) -> Unit) {
        val matchroomRef =
            FirebaseDatabase.getInstance()
                .getReference("chatrooms/${matchId}/chatRoom")
        matchroomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val data = dataSnapshot.getValue(ChatRoom::class.java)
                    callback(data!!)
                } else {
                    callback(null)
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
            removeListener(user.participateMatchId)
            callback()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to remove node", e)
        }
    }
}