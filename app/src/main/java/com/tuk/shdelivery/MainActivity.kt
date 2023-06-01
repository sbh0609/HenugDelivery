package com.tuk.shdelivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuk.shdelivery.databinding.ActivityMainBinding // import your view binding class
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), CoroutineScope  {
    // Instantiate view binding class
    private lateinit var binding: ActivityMainBinding

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()
        val dao = UserDao()

        binding.btn1.setOnClickListener{
            var userId: String = "4"
            var userName: String = "d"
            var participateMatchId: Long =0
            var userPoint: Long = 40

            var ex = User(userId, userName, participateMatchId, userPoint)

            dao.addUser(ex)
        }
        binding.btn2.setOnClickListener {
            launch {
                val userId = "1"
                val result: User? = dao.getUser(userId)

                if (result != null) {
                    val userAge = result.userName
                    if (!userAge.isNullOrEmpty()) {
                        binding.text1.text = userAge
                    } else {
                        binding.text1.text = "User age is null or empty"
                    }
                } else {
                    binding.text1.text = "User not found"
                }
            }
        }
    }
}
