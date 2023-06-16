package com.tuk.shdelivery

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.tuk.shdelivery.Data.ChargePoint
import com.tuk.shdelivery.Data.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

object UserDao {
    private var userRef: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        userRef = db.getReference("user")
    }
    //테스트 시 주석 풀기
//    fun getUserRef(): DatabaseReference? {
//        return userRef
//    }

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
    fun getUser(userId: String, callback: (User?) -> Unit) {
        userRef?.child(userId)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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
    fun updateUser(user: User?, callback: () -> Unit): Unit {
        userRef?.child(user!!.userId.toString())?.setValue(user)?.addOnSuccessListener {
            callback()
        }
    }

    fun userListener(user: User, callback: (user: User?) -> Unit) {
        userRef?.child(user.userId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    callback(user!!)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    fun saveChargeRequest(userId: String, chargePoint: ChargePoint, onComplete: () -> Unit) {
        // 데이터베이스 참조 얻기
        val db = FirebaseDatabase.getInstance().reference

        // 현재 날짜와 시간을 이용하여 고유한 충전 요청 ID 생성
        val chargeRequestId = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())

        // 데이터 저장
        db.child("user").child(userId).child("ChargePoint").child(chargeRequestId).setValue(chargePoint)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 작업 성공
                    onComplete()
                } else {
                    // 작업 실패
                    Log.e("UserDao", "Error saving charge request", task.exception)
                }
            }
    }
}