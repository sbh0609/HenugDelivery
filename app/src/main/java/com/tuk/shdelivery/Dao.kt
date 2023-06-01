package com.tuk.shdelivery

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserDao {
    private var userRef: DatabaseReference? = null

    init{
        val db = FirebaseDatabase.getInstance()
        userRef = db.getReference("user")
    }

    //등록
    fun addUser(user: User?): Task<Void>? {
        return userRef?.child(user?.userId.toString())?.setValue(user)
    }
    //조회
    suspend fun getUser(userId: String): User? = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<User?>()

        userRef?.child("users")?.child(userId)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    deferred.complete(user)
                } else {
                    deferred.complete(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                deferred.completeExceptionally(databaseError.toException())
            }
        })

        deferred.await()
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