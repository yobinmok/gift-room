package com.example.capstone_giftcon

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.capstone_giftcon.databinding.ActivitySettingBinding
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.ServiceApi
import com.example.capstone_giftcon.network.Utils
import com.example.capstone_giftcon.ui.dialog.DeleteDialogFragment
import com.example.capstone_giftcon.ui.dialog.NameDialogFragment
import com.example.capstone_giftcon.ui.dialog.PeriodDialogFragment
import com.example.capstone_giftcon.ui.dialog.PwdDialogFragment
import com.example.capstone_giftcon.ui.viewModel.UserViewModel
import com.google.android.material.navigation.NavigationView

class SettingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivitySettingBinding
    private var pushOnOff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.init(applicationContext)

        setSupportActionBar(binding.myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        binding.navigationView.setNavigationItemSelectedListener(this as NavigationView.OnNavigationItemSelectedListener)

        binding.mainBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        pushOnOff = binding.pushSwitch.isChecked
        binding.pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("onoffBtn", "알림 on!")
                pushOnOff = true
                binding.pushTerm.setTextColor(Color.parseColor("#FF000000"))
                binding.pushTerm.isEnabled = true
            } else {
                Log.d("onoffBtn", "알림 OFF!")
                pushOnOff = false
                binding.pushTerm.setTextColor(Color.parseColor("#a3a3a3"))
                binding.pushTerm.isEnabled = false
            }
        }
        if (!pushOnOff) {
            binding.pushTerm.setTextColor(Color.parseColor("#a3a3a3"))
        } else {
            binding.pushTerm.setTextColor(Color.parseColor("#FF000000"))
        }

        binding.nameModify.setOnClickListener {
            val nameDialog = NameDialogFragment()
            nameDialog.show(supportFragmentManager, "nameDialog")
        }

        binding.pwdModify.setOnClickListener {
            val pwdDialog = PwdDialogFragment()
            pwdDialog.show(supportFragmentManager, "pwdDialog")
        }

        binding.deleteAccount.setOnClickListener {
            DeleteDialogFragment().show(supportFragmentManager, "pwdDialog")
        }

        binding.pushTerm.setOnClickListener {
            PeriodDialogFragment(pushOnOff).show(supportFragmentManager, "periodDialog")
        }


        binding.logout.setOnClickListener(View.OnClickListener {
            Utils.clearToken()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() { //뒤로가기 했을 때
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val intent =
                Intent(getApplicationContext(), MainActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_item1) {
            val intent = Intent(getApplicationContext(), MypageActivity::class.java) // 내 계정으로 화면 전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (id == R.id.menu_item2) {
            val intent = Intent(getApplicationContext(), BoardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (id == R.id.menu_item4) {
            val intent = Intent(getApplicationContext(), SettingActivity::class.java) // 설정으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        val drawer: DrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}