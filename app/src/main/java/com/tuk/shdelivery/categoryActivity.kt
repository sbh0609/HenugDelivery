package com.tuk.shdelivery

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes.Margins
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import com.tuk.shdelivery.custom.ToastCustom
import com.tuk.shdelivery.databinding.CategoryBinding
import com.tuk.shdelivery.databinding.CategoryIconBinding
import com.tuk.shdelivery.databinding.MatchRoomBinding

class categoryActivity : AppCompatActivity() {

    val binding by lazy { CategoryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바 넣기
        setSupportActionBar(binding.toolbar)
        //뒤로가기 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //카테고리 넣는 함수
        createCategory();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createCategory() {
        val drawList = arrayListOf<Int>(
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval,
            R.drawable.zokval
        )
        var margin = 5
        val width = intent.getIntExtra(
            "width",
            20
        ) / binding.grid.columnCount - binding.grid.columnCount * 2 * margin
        val layoutParams = LinearLayout.LayoutParams(width, width)
        var a = 1
        for (i in drawList) {
            val newBd = CategoryIconBinding.inflate(
                LayoutInflater.from(binding.grid.context),
                binding.grid,
                false
            )

            newBd.ImageView.setImageResource(i)
            newBd.ImageView.layoutParams = layoutParams

            newBd.TextView.text = a.toString()

            newBd.root.setOnClickListener {
                ToastCustom.toast(applicationContext, newBd.TextView.text.toString() + "선택하였습니다.")
                intent.putExtra("category", newBd.TextView.text)
                setResult(0, intent)
                finish()
            }

            binding.grid.addView(newBd.root)
            a = a + 1
        }
    }
}