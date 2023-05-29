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
    fun add(user: User?): Task<Void>? {
        return userRef!!.push().setValue(user)
    }
    //조회
    fun getUserList(user: User?): Task<DataSnapshot>? {
        return userRef?.child(user!!.userId.toString())?.get()?.addOnSuccessListener {
            Log.i("Firebase", "value ${it.value}")
        }?.addOnFailureListener{
            Log.e("Fail","error", it)
        }
    }
    //삭제
    fun del(user: User?): Task<Void>? {
        return userRef?.child(user!!.userId.toString())?.removeValue()

    }
    //수정
    fun update(user: User?): Task<Void>? {
        return userRef?.child(user!!.userId.toString())?.setValue(user)
    }
}