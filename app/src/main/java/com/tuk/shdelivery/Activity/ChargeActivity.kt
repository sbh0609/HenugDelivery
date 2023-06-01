package com.tuk.shdelivery.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import com.tuk.shdelivery.R
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
        binding.point.addTextChangedListener(object : TextWatcher {
            private val decimalFormat = DecimalFormat("#,###")
            private var result: String = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if(!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != result && charSequence.toString() != "원"){
                    result = decimalFormat.format(charSequence.toString().replace("원","").replace(",","").toDouble())
                    result += "원"
                    binding.point.setText(result);
                    binding.point.setSelection(result.length-1);
                }
                if(charSequence.toString() == "원"){
                    result = ""
                    binding.point.setText(result);
                }
            }

            override fun afterTextChanged(s: Editable) {}

        })

        //액션바 설정
        createActionBar()

        //취소 버튼 클릭 리스너 등록
        binding.cancle.setOnClickListener { finish() }

        //생성 버튼 클릭 리스너 등록
        binding.done.setOnClickListener {  }
    }

    private fun createActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}