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
    private val handleData = HandleData()
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

                    if (roomName.isNotEmpty() && foodType.isNotEmpty() && storeName.isNotEmpty() && time.isNotEmpty()) {
//                        createChatroom(roomName,foodType, storeName, time)
                        val chatroom = Room(
                            name = roomName,
                            foodType = foodType,
                            storeName = storeName,
                            time = time
                        )
                        handleData.createChatroom(chatroom)
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
}
