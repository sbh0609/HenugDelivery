package com.tuk.shdelivery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuk.shdelivery.databinding.LoginBinding


class LoginActivity : AppCompatActivity() {
    // 바인딩 객체 생성
    lateinit var bd: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = LoginBinding.inflate(layoutInflater)

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
