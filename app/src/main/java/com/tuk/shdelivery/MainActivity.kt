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


        /**
         * add, get, del, update에 대한 예시
         * 매개변수는 자체적으로 수정할 수 있다
         * get을 사용하기 위햐서 coroutine 세팅을 해야한다
         **/


        binding.btn1.setOnClickListener{
            var userId: String = "6"
            var userName: String = "f"

            var ex = User(userId, userName, "", 0, 0)

            dao.addUser(ex)
        }
        binding.btn2.setOnClickListener {
            launch {
                val userId = "4"
                val result: User? = dao.getUser(userId)

                if (result != null) {
                    val userPoint = result.userPoint
                    if (!userId.isNullOrEmpty()) {
                        binding.text1.text = userPoint.toString()
                    } else {
                        binding.text1.text = "User age is null or empty"
                    }
                } else {
                    binding.text1.text = "User not found"
                }
            }
        }
        binding.btn3.setOnClickListener{
            var userId: String = "4"

            dao.delUser(userId)
        }

        binding.btn4.setOnClickListener{
            var userId: String = "4"
            var userName: String = "d"
            var participateMatchId: String ="321"
            var userPoint: Long = 50

            var ex = User(userId, userName, participateMatchId, userPoint)

            dao.updateUser(ex)
        }
    }
}
