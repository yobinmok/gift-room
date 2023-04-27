package com.example.capstone_giftcon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.ServiceApi

class FindPwdActivity : AppCompatActivity() {
    private var service: ServiceApi = RetrofitApi.retrofitService
    var toolbar: Toolbar? = null
    var id: EditText? = null
    var find_button: Button? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_pwd)
        toolbar = findViewById<View>(R.id.my_toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        id = findViewById<EditText>(R.id.id)
        find_button = findViewById<Button>(R.id.find_button)
        find_button!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(getApplicationContext(), LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
    }
}