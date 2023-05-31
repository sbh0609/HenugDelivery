package com.tuk.shdelivery;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import com.google.firebase.database.*

class MessageUtil {
//    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//
//    fun saveMessage(roomId: String, message: Message) {
//        val ref: DatabaseReference = database.getReference("rooms/$roomId/messages")
//
//        // generate unique key for each message
//        val key = ref.push().key
//        if (key == null) {
//            Log.w(TAG, "Couldn't get push key for message")
//            return
//        }
//
//        ref.child(key).setValue(message)
//                .addOnSuccessListener {
//            Log.d(TAG, "Message saved successfully")
//        }
//            .addOnFailureListener {
//            Log.e(TAG, "Message saving failed", it)
//        }
//    }
//
//    fun loadMessages(roomId: String, listener: ValueEventListener) {
//        val ref: DatabaseReference = database.getReference("rooms/$roomId/messages")
//        ref.addValueEventListener(listener)
//    }
//
//    companion object {
//        const val TAG = "MessageUtil"
//    }
}
