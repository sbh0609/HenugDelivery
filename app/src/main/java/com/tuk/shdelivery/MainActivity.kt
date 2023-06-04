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
/*
        binding.orderBtn.setOnClickListener{
            launch {
                val funcd = matchFunc()
                val id = "2018156010" //방장 아이디
                val id2 = "2018156011" //오더 수락 || 취소 아이디
                val result: Fdatabase? = funcd.getDb(id) //객체 호출

                if (result != null) {
                    val orderAccept = result.orderAcceptNum + 1 //order수락명수 +1
                    funcd.addOrderUser(id, id2) //order수락 유저 노드 추가
                    //funcd.delOrderUser(id, id2) oder취소 유저 노드 제거
                } else {
                    binding.text1.text = "User not found"
                }
            }
        }
*/
    }
}
