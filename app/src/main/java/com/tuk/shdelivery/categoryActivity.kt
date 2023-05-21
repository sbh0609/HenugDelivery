package com.tuk.shdelivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.tuk.shdelivery.databinding.CategoryBinding

class categoryActivity : AppCompatActivity() {

    val binding by lazy { CategoryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바 넣기
        setSupportActionBar(binding.toolbar)
        //뒤로가기 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}