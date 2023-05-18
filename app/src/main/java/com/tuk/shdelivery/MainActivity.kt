package com.tuk.shdelivery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.tuk.shdelivery.databinding.ActivityMainBinding // import your view binding class
import com.tuk.shdelivery.databinding.DialogCreateChatroomBinding


class MainActivity : AppCompatActivity() {
    // Instantiate view binding class
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.makeChat.setOnClickListener {
            val dialogBinding = DialogCreateChatroomBinding.inflate(layoutInflater)

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setView(dialogBinding.root)
                .setCancelable(false)
                .setPositiveButton("Create") { dialog, _ ->
                    val roomName = dialogBinding.editTextRoomName.text.toString()
                    val foodType = dialogBinding.editTextFoodType.text.toString()
                    val storeName = dialogBinding.editTextStoreName.text.toString()
                    val time = dialogBinding.editTextTime.text.toString()

                    if (roomName.isNotEmpty()&&foodType.isNotEmpty() && storeName.isNotEmpty() && time.isNotEmpty()) {
                        createChatroom(roomName,foodType, storeName, time)
                    } else {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Create Chatroom")
            alert.show()
        }
        binding.listChatRoom.setOnClickListener {
            val intent = Intent(this, ListRoomActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createChatroom(roomName:String,foodType: String, storeName: String, time: String) {
        // Create chatroom object
        val chatroom = hashMapOf(
            "roomName" to roomName,
            "foodType" to foodType,
            "storeName" to storeName,
            "time" to time
        )

        // Get a reference to the database
        val database = FirebaseDatabase.getInstance().reference

        // Generate a new chatroom ID
        val chatroomId = database.child("chatrooms").push().key

        if (chatroomId != null) {
            // Save the chatroom to the database
            database.child("chatrooms").child(chatroomId).setValue(chatroom)
                .addOnSuccessListener {
                    // Chatroom was created successfully
                    Toast.makeText(this, "Chatroom created", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // An error occurred
                    Toast.makeText(this, "Failed to create chatroom", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
