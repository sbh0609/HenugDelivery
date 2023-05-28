package com.tuk.shdelivery.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tuk.shdelivery.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    // 바인딩 객체 생성
    lateinit var bd: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(bd.root)

        bd.login.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        bd.logout.setOnClickListener {
            finish()
        }
    }
}
