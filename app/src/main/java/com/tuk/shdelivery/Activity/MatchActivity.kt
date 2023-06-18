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
    val userDao = UserDao
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

    private fun enterButton() {
        fun showToast(message: String) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }

        binding.done.isEnabled = false

        val user = intent.getSerializableExtra("user") as User
        val selectData = intent.getSerializableExtra("selectMatchData") as MatchRoomData

        // If the user is not in a match room
        if (user.participateMatchId.isEmpty()) {
            MatchDao.getParticipatingMatch(selectData.id) { matchRoomData ->
                MatchDao.getChatRoomData(selectData.id) { chatRoomData ->
                    if (matchRoomData == null) {
                        showToast("사라진 방 입니다.")
                        finish()
                    } else {
                        intent.putExtra("selectChatRoom", chatRoomData)
                        // 아직 배달이 시작되지 않았으면
                        if (matchRoomData.id != "start") {
                            user.participateMatchId = selectData.id
                            intent.putExtra("user", user)
                            val updateFields = mapOf("participateMatchId" to selectData.id)
                            UserDao.updateUserFields(user.userId, updateFields) {
                                MatchDao.joinUserMatchRoom(user, selectData) {
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                            }
//                            userDao.updateUser(user) {
//                                MatchDao.joinUserMatchRoom(user, selectData) {
//                                    setResult(RESULT_OK, intent)
//                                    finish()
//                                }
//                            }

                        } else {
                            // 배달이 시작됬으면
                            showToast("배달이 시작된 방입니다.")
                            finish()
                        }
                    }
                }
            }
        } else {
            showToast("매칭방에 이미 입장중 입니다.")
            finish()
        }
    }

    private fun setText() {
        val data = intent.getSerializableExtra("selectMatchData") as MatchRoomData
        binding.deliveryTime.text =
            SimpleDateFormat("MM/dd(E)   a K : mm", Locale.KOREAN).format(
                Calendar.getInstance().apply { timeInMillis = data.deliveryTime }.time
            )
        binding.toolbar.subtitle =
            SimpleDateFormat("MM/dd(E)   a h시 mm분", Locale.KOREAN).format(
                Calendar.getInstance().apply { timeInMillis = data.createTime }.time
            ) + " 작성글"
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