package com.stonecoders.stockinteligenterfid

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.stonecoders.stockinteligenterfid.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.progressBar.isIndeterminate = true
        this.actionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        SystemClock.sleep(2500)
        // TODO: Add network calls here to update
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
}