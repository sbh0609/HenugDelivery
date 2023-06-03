package com.tuk.shdelivery.Activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
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
            var user = intent.getSerializableExtra("user") as User
            //매칭방이 없어야 가능
            if(user.participateMatchId == ""){
                user.participateMatchId = (intent.getSerializableExtra("selectMatchData") as MatchRoomData).id
                intent.putExtra("user",user)
                userDao.updateUser(user){
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else{
                Toast.makeText(applicationContext,"매칭방에 이미 입장중 입니다.",Toast.LENGTH_SHORT).show()
            }
        }

    }

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