package com.tuk.shdelivery

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class UserDao {
    private var userRef: DatabaseReference? = null

    init{
        val db = FirebaseDatabase.getInstance()
        userRef = db.getReference("user")
    }

    //등록
    fun addUser(user: User?): Task<Void>? {
        return userRef!!.push().setValue(user)
    }
    //조회
    fun getUser(user: User?): Task<DataSnapshot>? {
        return userRef?.child(user!!.userId.toString())?.get()?.addOnSuccessListener {
            Log.i("Firebase", "value ${it.value}")
        }?.addOnFailureListener{
            Log.e("Fail","error", it)
        }
    }
    //삭제
    fun delUser(user: User?): Task<Void>? {
        return userRef?.child(user!!.userId.toString())?.removeValue()

    }
    //수정
    fun updateUser(user: User?): Task<Void>? {
        return userRef?.child(user!!.userId.toString())?.setValue(user)
    }
}

class MatchDao {
    private var matchRef: DatabaseReference? = null

    init{
        val db = FirebaseDatabase.getInstance()
        matchRef = db.getReference("match")
    }

    //등록
    fun addMatch(matchroom: MatchRoom?): Task<Void>? {
        return matchRef!!.push().setValue(matchroom)
    }
    //조회
    fun getMatch(matchroom: MatchRoom?): Task<DataSnapshot>? {
        return matchRef?.child(matchroom!!.matchRoomId.toString())?.get()?.addOnSuccessListener {
            Log.i("Firebase", "value ${it.value}")
        }?.addOnFailureListener{
            Log.e("Fail","error", it)
        }
    }
    //삭제
    fun delMatch(matchroom: MatchRoom?): Task<Void>? {
        return matchRef?.child(matchroom!!.matchRoomId.toString())?.removeValue()

    }
    //수정
    fun updateMatch(matchroom: MatchRoom?): Task<Void>? {
        return matchRef?.child(matchroom!!.matchRoomId.toString())?.setValue(matchroom)
    }
    //전체조회
    fun
}

class ChatRoomDao {
    private var chatRoomRef: DatabaseReference? = null

    init{
        val db = FirebaseDatabase.getInstance()
        chatRoomRef = db.getReference("chatRoom")
    }

    //등록
    fun addChatRoom(chatroom: ChatRoom?): Task<Void>? {
        return chatRoomRef!!.push().setValue(chatroom)
    }
    //조회
    fun getChatRoom(chatroom: ChatRoom?): Task<DataSnapshot>? {
        return chatRoomRef?.child(chatroom!!.chatId.toString())?.get()?.addOnSuccessListener {
            Log.i("Firebase", "value ${it.value}")
        }?.addOnFailureListener{
            Log.e("Fail","error", it)
        }
    }
    //삭제
    fun delChatRoom(chatroom: ChatRoom?): Task<Void>? {
        return chatRoomRef?.child(chatroom!!.chatId.toString())?.removeValue()

    }
    //수정
    fun updateChatRoom(chatroom: ChatRoom?): Task<Void>? {
        return chatRoomRef?.child(chatroom!!.chatId.toString())?.setValue(chatroom)
    }
}

class ChatDao {
    private var chatRef: DatabaseReference? = null

    init{
        val db = FirebaseDatabase.getInstance()
        chatRef = db.getReference("chat")
    }

    //등록
    fun addChat(chatroom: ChatRoom?): Task<Void>? {
        return chatRef!!.push().setValue(chatroom)
    }
    //조회
    fun getChat(chat: Chat?): Task<DataSnapshot>? {
        return chatRef?.child(chat!!.chatId.toString())?.get()?.addOnSuccessListener {
            Log.i("Firebase", "value ${it.value}")
        }?.addOnFailureListener{
            Log.e("Fail","error", it)
        }
    }
}