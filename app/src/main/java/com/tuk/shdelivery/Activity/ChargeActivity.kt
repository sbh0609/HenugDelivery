package com.tuk.shdelivery.Activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tuk.shdelivery.Data.ChargePoint
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.databinding.ActivityChargeBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
//        binding.done.setOnClickListener { done() }
        binding.done.setOnClickListener { sendChargeRequest() }
    }
    private fun sendChargeRequest() {
        val chargeAmount = binding.point.text.toString().replace("원", "").replace(",", "").toLong()
        val user = FirebaseAuth.getInstance().currentUser!!

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val formatterKey = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
        val chargeDateKey = formatterKey.format(Date())
        val chargePoint = ChargePoint(user.uid, chargeAmount, 0) // chargeDate는 이제 키로 사용되므로 ChargePoint 객체에 포함시키지 않습니다.

        // Use chargeDateKey for each charge request
        val databaseReference = firebaseDatabase.getReference("users").child(user.uid).child("ChargePoint").child(chargeDateKey)

        databaseReference.setValue(chargePoint)
            .addOnSuccessListener {
                // "충전 요청이 성공적으로 전송되었습니다." 토스트 메시지 표시
            }
            .addOnFailureListener {
                // "충전 요청 전송에 실패하였습니다. 다시 시도해주세요." 토스트 메시지 표시
            }
    }


    private fun done(inputPoint: Long) {
        val user = intent.getSerializableExtra("user") as User
        user.userPoint += inputPoint
        intent.putExtra("user",user)

        UserDao().updateUser(user) {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    // 사용자가 입력한 포인트를 처리하고, UserDao를 통해 사용자 정보를 업데이트합니다.
//    private fun done() {
//        val user = intent.getSerializableExtra("user") as User
//        val inputPoint = binding.point.text.toString().replace("원", "").replace(",", "").toLong()
//        user.userPoint += inputPoint
//        intent.putExtra("user",user)
//        intent.putExtra("inputPoint", inputPoint)
//
//        UserDao().updateUser(user) {
//            setResult(Activity.RESULT_OK, intent)
//            finish()
//        }
//    }

    // 사용자가 입력한 금액을 포맷팅하여 보여주는 함수입니다.
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

    // 툴바를 액션바로 설정하고 뒤로 가기 버튼을 활성화하는 함수입니다.
    private fun createActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 액션바의 뒤로 가기 버튼을 눌렀을 때의 동작을 정의합니다.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}