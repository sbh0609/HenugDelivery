package com.tuk.shdelivery.Activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
        //백업
        val user = intent.getSerializableExtra("user") as User
        val inputPoint = binding.point.text.toString().replace("원", "").replace(",", "").toLong()
        user.userPoint += inputPoint
        intent.putExtra("user",user)
        intent.putExtra("inputPoint", inputPoint)
        //충전 하기
        UserDao.updateUser(user) {
            setResult(Activity.RESULT_OK, intent)
            finish()
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