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
import com.tuk.shdelivery.Data.User
import com.tuk.shdelivery.UserDao
import com.tuk.shdelivery.databinding.ActivityChargeBinding
import java.text.DecimalFormat
import com.tuk.shdelivery.Data.ChargeRequest
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
        // 사용자가 입력한 충전 금액을 가져옵니다.
        val chargeAmount = binding.point.text.toString().replace("원", "").replace(",", "").toLong()

        // 현재 로그인된 사용자를 가져옵니다.
        val user = FirebaseAuth.getInstance().currentUser!!

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("ChargeRequest")

        val chargeRequest = ChargeRequest(user.uid, chargeAmount) //ChargeRequest는 사용자 ID와 충전 요청 금액을 포함하는 데이터 클래스입니다.

        databaseReference.child(user.uid).setValue(chargeRequest)
            .addOnSuccessListener {
                //요청 정보를 데이터베이스에 성공적으로 저장한 후, done 함수를 호출합니다.
                done(chargeAmount)
            }
            .addOnFailureListener {
                //데이터베이스에 요청 정보를 저장하는데 실패한 경우의 작업을 여기에 코딩합니다.
                //예를 들어, "충전 요청 전송에 실패하였습니다. 다시 시도해주세요."라는 토스트 메시지를 표시할 수 있습니다.
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