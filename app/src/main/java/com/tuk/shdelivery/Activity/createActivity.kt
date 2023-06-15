package com.tuk.shdelivery.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tuk.shdelivery.Data.MatchDao
import com.tuk.shdelivery.Data.MatchRoomData
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.custom.Data
import com.tuk.shdelivery.custom.DeliverTime
import com.tuk.shdelivery.databinding.ActivityCreateBinding
import java.util.*
import android.R as r

class createActivity : AppCompatActivity() {
    val binding by lazy { ActivityCreateBinding.inflate(layoutInflater) } // Binding 객체 초기화
    val categoryMap = Data.category()// 카테고리 정보를 담은 map

    var deliveryCalendar = Calendar.getInstance() // 배달 일정을 담은 캘린더 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //액션바 설정
        createActionBar()

        //스피너 선택 설정/
        setcategorySpinner()

        //타임피커다이얼로그 생성
        createTimePicker()

        //데이트 피커 생성
        createaDatePicker()

        //취소 버튼 클릭 리스너 등록
        binding.cancle.setOnClickListener { finish() }

        //생성 버튼 클릭 리스너 등록
        binding.done.setOnClickListener { done() }
    }

    //완료 버튼을 클릭했을 때의 동작을 정의하는 함수
    private fun done() {
        binding.done.isEnabled = false
        binding.cancle.isEnabled = false
        val nowTime = Calendar.getInstance().apply {
            set(Calendar.SECOND, 59)
        }

        //입력 내용 검사
        deliveryCalendar.set(Calendar.SECOND, 0)
        if (binding.description.text.toString() == "" || nowTime >= deliveryCalendar) {
            var text =
                if (binding.description.text.toString() == "")
                    "내용을 입력하세요"
                else
                    "현재 시간을 확인 하세요"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            binding.done.isEnabled = true
            binding.cancle.isEnabled = true
            return
        }
        // 매치 데이터 생성
        val createData = MatchRoomData(
            (intent.getSerializableExtra("user") as User).userId,
            binding.category.selectedItem as String,
            deliveryCalendar.timeInMillis,
            binding.description.text.toString(),
            0,
            nowTime.timeInMillis,
            binding.store.text.toString()
        )
        // 매치 데이터를 서버에 등록
        MatchDao.createMatchingRoom((intent.getSerializableExtra("user") as User), createData) {
            MatchDao.joinUserMatchRoom(intent.getSerializableExtra("user") as User, createData) {
                intent.putExtra("createData", createData)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
    //데이트 피커 생성 함수
    private fun createaDatePicker() {
        val calendar = Calendar.getInstance()
        deliveryCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        deliveryCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        deliveryCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))

        //텍스트 바꿔준다.
        binding.day.text = DeliverTime(deliveryCalendar).getDay()
        fun showDatePicker(context: Context) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val listener =
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val current = Calendar.getInstance()
                    val selected = Calendar.getInstance().apply {
                        set(Calendar.YEAR, selectedYear)
                        set(Calendar.MONTH, selectedMonth)
                        set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                    }


                    if (selected.before(current)) {
                        Toast.makeText(this, "이전 날짜는 선택 할수 없습니다.", Toast.LENGTH_SHORT).show()
                        showDatePicker(context)  // 다시 데이트 피커 다이얼로그를 띄웁니다.
                    } else {
                        //calender 객체 등록한다.
                        deliveryCalendar.set(Calendar.YEAR, selectedYear)
                        deliveryCalendar.set(Calendar.MONTH, selectedMonth)
                        deliveryCalendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)

                        //텍스트 바꿔준다.
                        binding.day.text = DeliverTime(deliveryCalendar).getDay()
                    }
                }

            val datePickerDialog = DatePickerDialog(context, listener, year, month, day)
            datePickerDialog.show()
        }

        //데이트 피커 다이얼로그 생성성
        binding.day.setOnClickListener { showDatePicker(this) }
    }

    private fun createTimePicker() {
        val current = Calendar.getInstance()

        //deliveryCalerdar설정
        deliveryCalendar.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY))
        deliveryCalendar.set(Calendar.MINUTE, current.get(Calendar.MINUTE))


        //텍스트 설정
        binding.deliveryTime.text = DeliverTime(deliveryCalendar).getTime()

        fun showTimePicker(context: Context) {


            val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val select = Calendar.getInstance().apply {
                    set(Calendar.YEAR, deliveryCalendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, deliveryCalendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, deliveryCalendar.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }

                if (select.before(current)) {
                    Toast.makeText(context, "이전 시간은 선택하실 수 없습니다.", Toast.LENGTH_SHORT).show()
                    showTimePicker(context)  // 다시 타임 피커 다이얼로그를 띄웁니다.
                } else {
                    //텍스트 바꿔주기
                    binding.deliveryTime.text = DeliverTime.getTime(selectedHour, selectedMinute)
                    //calendar 등록
                    deliveryCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                    deliveryCalendar.set(Calendar.MINUTE, selectedMinute)
                }
            }

            val timePickerDialog =
                TimePickerDialog(
                    context,
                    listener,
                    deliveryCalendar.get(Calendar.HOUR_OF_DAY),
                    deliveryCalendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(this)
                )
            timePickerDialog.show()
        }

        // 클릭 리스너를 설정
        binding.deliveryTime.setOnClickListener { showTimePicker(this) }
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
    }

    private fun createActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //뒤로가기 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}