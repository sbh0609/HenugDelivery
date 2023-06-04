package com.tuk.shdelivery

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class matchFunc {
    /**
     * input: fdatabase <Fdatabase>
     * output: Void <Void>
     * fdatabase 객체를 삽입한다
     **/
    fun addDb(fdatabase: Fdatabase) {
        database?.child("chatroom")?.child(fdatabase.id.toString())?.setValue(fdatabase)
    }
    /**
     * input: id <String>
     * output: result <Fdatabse>
     * id를 기반으로 해당하는 chat룸을 찾는다
     **/
    suspend fun getDb(id: String): Fdatabase? = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<Fdatabase?>()

        database?.child(id.toString())?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val result = dataSnapshot.getValue(Fdatabase::class.java)
                    deferred.complete(result)
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
     * input: ownerId <String>, orderId <String>
     * output: Void <Void>
     * owenerId를 기반으로 chat룸을 찾아서 orderAcceptNum 밑에 orderId를 노드로 추가한다
     **/
    fun addOrderUser(ownerId: String?, orderId: String) {
        database?.child("chatroom")?.child(ownerId.toString())?.child("orderAcceptNum")
            ?.setValue(orderId)

    }
    /**
     * input: ownerId <String>, orderId <String>
     * output: Void <Void>
     * ownerId를 기반으로 chat룸을 찾아서 orderAcceptNum 밑에 orderId를 제거한다
     **/
    fun delOrderUser(ownerId: String?, orderId: String) {
        database?.child("chatroom")?.child(ownerId.toString())?.child("orderAcceptNum")
            ?.child(orderId.toString())?.removeValue()
    }
}