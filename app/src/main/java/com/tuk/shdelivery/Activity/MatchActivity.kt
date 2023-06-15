package com.tuk.shdelivery.Activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.databinding.ActivityMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class MatchActivity : AppCompatActivity() {
    val binding by lazy { ActivityMatchBinding.inflate(layoutInflater) }
    val userDao = UserDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //액션바 세팅
        createActionBar()

        //화면 텍스트 세팅
        setText()

        binding.cancle.setOnClickListener {
            finish()
        }
        //매칭방 입장
        binding.done.setOnClickListener {
            enterButton()
        }
    }

    //입장 버튼을 클릭했을 때 동작을 정의하는 함수
    private fun enterButton() {
        fun showToast(message: String) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }

        binding.done.isEnabled = false

        val user = intent.getSerializableExtra("user") as User
        val selectData = intent.getSerializableExtra("selectMatchData") as MatchRoomData

        // 사용자가 매칭방에 입장해있는지 확인
        if (user.participateMatchId.isEmpty()) {
            MatchDao.getChatRoomData(selectData.id) { chatRoomData ->
                if (chatRoomData == null) {
                    showToast("사라진 방 입니다.")
                    finish()
                } else {
                    intent.putExtra("selectChatRoom", chatRoomData)
                    // If the room is not full
                    if (chatRoomData.participatePeopleId.size != chatRoomData.orderAcceptNum) {
                        user.participateMatchId = selectData.id
                        intent.putExtra("user", user)
                        userDao.updateUser(user) {
                            MatchDao.joinUserMatchRoom(user, selectData) {
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                        }
                    } else {
                        // If the room is already in delivery state
                        showToast("배달이 시작된 방입니다.")
                        finish()
                    }
                }
            }
            // 이미 있으면 트스트 메시지 출력 후 화면 종료
        } else {
            showToast("매칭방에 이미 입장중 입니다.")
            finish()
        }
    }
    // 화면의 텍스트를 설정하는 함수
    private fun setText() {
        val data = intent.getSerializableExtra("selectMatchData") as MatchRoomData
        binding.deliveryTime.text =
            SimpleDateFormat("MM/dd(E)   a K : mm", Locale.KOREAN).format(Calendar.getInstance().apply { timeInMillis = data.deliveryTime }.time)
        binding.toolbar.subtitle =
            SimpleDateFormat("MM/dd(E)   a h시 mm분", Locale.KOREAN).format(Calendar.getInstance().apply { timeInMillis = data.createTime }.time) + " 작성글"
        binding.description.text = data.description
        binding.storeName.text = data.storeName
        binding.tag.text = data.menu
        binding.tagImage.setImageResource(Data.category()[data.menu]!!)
        binding.count.text = data.count.toString() + "명"
    }

    private fun createActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}