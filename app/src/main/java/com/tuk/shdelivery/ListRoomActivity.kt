package com.tuk.shdelivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tuk.shdelivery.databinding.ActivityListRoomBinding

class ListRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListRoomBinding // Replace with your actual binding class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityListRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get a reference to the database
        val database = FirebaseDatabase.getInstance().reference

        // Get a reference to the chatrooms node
        val chatroomsRef = database.child("chatrooms")

        // Attach a ValueEventListener to the chatrooms
        chatroomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the chatroom list
                binding.chatroomList.removeAllViews()

                // Loop through all children of the chatrooms node
                for (chatroomSnapshot in snapshot.children) {
                    // Get the chatroom name
                    val chatroomName = chatroomSnapshot.child("storeName").getValue(String::class.java)

                    // Create a new Button for this chatroom
                    val chatroomButton = Button(this@ListRoomActivity)
                    chatroomButton.text = chatroomName
                    chatroomButton.setOnClickListener {
                        // Start ChatRoomActivity when this button is clicked
                        val intent = Intent(this@ListRoomActivity, ChatRoomActivity::class.java)
                        intent.putExtra("chatroomId", chatroomSnapshot.key)
                        startActivity(intent)
                    }

                    // Add the button to the chatroom list
                    binding.chatroomList.addView(chatroomButton)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        const val TAG = "ListRoomActivity"
    }
}
