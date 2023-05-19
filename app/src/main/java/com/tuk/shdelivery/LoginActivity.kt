package com.tuk.shdelivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuk.shdelivery.databinding.ActivityMainBinding // import your view binding class


class LoginActivity : AppCompatActivity() {
    // 바인딩 객체 생성
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
