package com.tuk.shdelivery.Activity

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.R
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.databinding.CreateBinding
import java.util.*
import kotlin.collections.ArrayList
import android.R as r

class createActivity : AppCompatActivity() {
    val binding by lazy { CreateBinding.inflate(layoutInflater) }
    val categoryMap = Data.category()
    var createData : MatchRoomData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //액션바 설정
        createActionBar()

        // 카테고리 선택 스피너 설정
        setcategorySpinner()

        //타임피커다이얼로그 생성
        binding.time.setOnClickListener{

            val calendar = Calendar.getInstance()
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _ , hour, minute ->
                    var isAfterNoon : Boolean = false
                    var hour2 = hour
                    if(hour>=12){
                        isAfterNoon = true
                        hour2 = hour - 12
                    }
                    if(isAfterNoon)
                    val selectedTime = String.format("%02d:%02d", hour, minute)
                    binding.time.text = selectedTime
                },
                hourOfDay,
                minute,
                DateFormat.is24HourFormat(this)
            )
            timePickerDialog.show()
        }
    }

    private fun setcategorySpinner() {
        val items = ArrayList<String>()
        for ((key, value) in categoryMap) {
            items.add(key)
        }
        // 어댑터 생성
        val adapter = ArrayAdapter(this, r.layout.simple_spinner_item, items)

        // 드롭다운 목록 레이아웃 설정
        adapter.setDropDownViewResource(r.layout.simple_spinner_dropdown_item)

        // 카테고리에 어댑터 설정
        binding.category.adapter = adapter

        // 선택 이벤트 처리
        binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // 선택된 항목 처리
                Log.d("spinner", items[position].toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
                Log.d("spinner", "nothing")
            }
        }
    }

    private fun createActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //뒤로가기 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}