package com.tuk.shdelivery.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuk.shdelivery.databinding.ActivityMatchBinding

class MatchActivity : AppCompatActivity() {

    val binding by lazy { ActivityMatchBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




    }
}