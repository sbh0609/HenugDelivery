package com.tuk.shdelivery.Activity

import android.app.Activity
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.tuk.shdelivery.Data.ChargePoint
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.R
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.databinding.ActivityChargeBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ChargeActivity : AppCompatActivity() {
    val binding by lazy { ActivityChargeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //포인트 입력 설정
        inputSetting()

        //액션바 설정
        createActionBar()

        //취소 버튼 클릭 리스너 등록
        binding.cancle.setOnClickListener { finish() }

        //생성 버튼 클릭 리스너 등록
        binding.done.setOnClickListener { done() }
    }

    private fun done() {
        // 사용자 정보 가져오기
        val user = intent.getSerializableExtra("user") as User

        // 사용자가 입력한 금액
        val inputPoint = binding.point.text.toString().replace("원", "").replace(",", "").toLong()

        // 충전 요청 데이터 생성
        val chargePoint = ChargePoint(
            userId = user.userId,
            chargeRequest = inputPoint
        )

        // 충전 요청을 데이터베이스에 저장
        UserDao.saveChargeRequest(user.userId, chargePoint) {
            Toast.makeText(this, "충전 요청이 전송되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
            //테스트 할 때 주석 풀기
            val chargePointRef = UserDao.getUserRef()?.child(user.userId)?.child("ChargePoint")
            chargePointRef?.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    // No action required
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    val chargePoint = dataSnapshot.getValue(ChargePoint::class.java)
                    if (chargePoint != null) {
                        when (chargePoint.chargeAllow) {
                            -1 -> Toast.makeText(this@ChargeActivity, "충전이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(this@ChargeActivity, "${chargePoint.chargeAllow} 원 충전이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    // No action required
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                    // No action required
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException())
                }
            })
        }
    }


    private fun inputSetting() {
        binding.point.addTextChangedListener(object : TextWatcher {
            private val decimalFormat = DecimalFormat("#,###")
            private var result: String = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int,
            ) {
                if (!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result && charSequence.toString() != "원") {
                    result = decimalFormat.format(
                        charSequence.toString().replace("원", "").replace(",", "").toDouble()
                    )
                    result += "원"
                    binding.point.setText(result);
                    binding.point.setSelection(result.length - 1);
                }
                if (charSequence.toString() == "원") {
                    result = ""
                    binding.point.setText(result);
                }
            }

            override fun afterTextChanged(s: Editable) {}

        })
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