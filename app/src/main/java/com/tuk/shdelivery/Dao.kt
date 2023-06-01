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
    /**
     * input: user <User 객체>
     * output: void <Void>
     * 카카오톡 userId,카카오톡 userName 을 작성하고, participateMatchId =0 userPoint =0 으로 들어간다
     **/
    fun addUser(user: User?): Task<Void>? {
        return userRef?.child(user?.userId.toString())?.setValue(user)
    }
    /**
     * input: userId <String>
     * output: User <User 객체>
     * userId를 넣으면 User객체를 리턴한다
     **/
    suspend fun getUser(userId: String): User? = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<User?>()

        userRef?.child(userId)?.addListenerForSingleValueEvent(object : ValueEventListener {
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
    /**
     * input: userId <String>
     * output: Void <Void>
     * userId를 넣으면 해당 user를 삭제한다
     **/
    fun delUser(userId: String?): Task<Void>? {
        return userRef?.child(userId.toString())?.removeValue()
    }
    /**
     * input: User <User 객체>
     * output: Void <Void>
     * User를 넣으면 해당 userId를 갖은 사용자의 정보를 수정한다
     **/
    fun updateUser(user: User?): Task<Void>? {
        return userRef?.child(user!!.userId.toString())?.setValue(user)
    }
}