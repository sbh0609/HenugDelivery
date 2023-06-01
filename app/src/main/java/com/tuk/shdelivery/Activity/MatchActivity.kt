package com.tuk.shdelivery.Activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.databinding.ActivityMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class MatchActivity : AppCompatActivity() {

    val binding by lazy { ActivityMatchBinding.inflate(layoutInflater) }

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

        binding.done.setOnClickListener {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    private fun setText() {
        val data = intent.getSerializableExtra("data") as MatchRoomData
        binding.deliveryTime.text =
            SimpleDateFormat("MM/dd(E) a K : mm", Locale.KOREAN).format(data.deliveryTime.time)
        binding.toolbar.subtitle =
            SimpleDateFormat("MM/dd(E) a h시 mm분", Locale.KOREAN).format(data.createTime.time) + " 작성글"
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
}