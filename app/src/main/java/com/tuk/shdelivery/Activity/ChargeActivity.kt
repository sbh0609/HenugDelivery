package com.tuk.shdelivery.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
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
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val userInput = s.toString().replace(",", "").replace("원", "")
                var currentAmount = ""
                if (currentAmount != userInput) {
                    currentAmount = userInput

                    val longval = currentAmount.toLongOrNull() ?: 0L
                    val formattedString = NumberFormat.getNumberInstance(Locale.US).format(longval)

                    // '원'을 추가합니다.
                    val resultString = "$formattedString 원"

                    // setting text after format to EditText
                    binding.point.removeTextChangedListener(this)
                    binding.point.setText(resultString)
                    binding.point.setSelection(resultString.length - 1)
                    binding.point.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable) {
                binding.point.removeTextChangedListener(this)

                try {
                    val originalString = s.toString()

                    val longval: Long = if (originalString.contains(",")) {
                        originalString.replace(",", "").replace("원", "").toLong()
                    } else {
                        originalString.replace("원", "").toLong()
                    }

                    val formatter = NumberFormat.getNumberInstance(Locale.US)
                    var formattedString = formatter.format(longval)

                    // '원'을 추가합니다.
                    formattedString += "원"

                    // setting text after format to EditText
                    binding.point.setText(formattedString)
                    binding.point.setSelection(binding.point.text.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.point.addTextChangedListener(this)
            }
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