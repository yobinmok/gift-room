package com.example.capstone_giftcon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone_giftcon.network.Utils.Companion.getAccessToken

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        try {
            val token = getAccessToken("failed")
            if (token != "failed") {
                moveMain(1)
            } else {
                moveLogin(1)
            }
        } catch (e: Exception) {
            moveLogin(1)
        }
    }

    private fun moveLogin(sec: Int) {
        Handler().postDelayed(Runnable {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish() //현재 액티비티 종료
        }, (1000 * sec).toLong()) // sec초 정도 딜레이를 준 후 시작
    }

    private fun moveMain(sec: Int) {
        Handler().postDelayed(Runnable {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish() //현재 액티비티 종료
        }, (1000 * sec).toLong()) // sec초 정도 딜레이를 준 후 시작
    }
}