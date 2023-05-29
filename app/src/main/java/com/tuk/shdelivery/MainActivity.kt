package com.tuk.shdelivery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.tuk.shdelivery.databinding.ActivityMainBinding // import your view binding class



class MainActivity : AppCompatActivity() {
    // Instantiate view binding class
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = UserDao()

        binding.btn1.setOnClickListener{
            //key 값 카카오 고유 코드
            val name = binding.edit1.text.toString()
            val age = binding.edit2.text.toString()

            val user = User("", name, age)

            dao.add(user)
        }

        binding.btn2.setOnClickListener{
            //key 값 카카오 고유 코드
            val name = binding.edit1.text.toString()
            val age = binding.edit2.text.toString()

            val user = User("", name, age)

            dao.getUserList(user)
        }

        binding.btn3.setOnClickListener{
            //key 값 카카오 고유 코드
            val name = binding.edit1.text.toString()
            val age = binding.edit2.text.toString()

            val user = User("", name, age)

            dao.del(user)
        }

        binding.btn4.setOnClickListener{
            //key 값 카카오 고유 코드
            val name = binding.edit1.text.toString()
            val age = binding.edit2.text.toString()

            val user = User("", name, age)

            dao.update(user)
        }
    }
}
