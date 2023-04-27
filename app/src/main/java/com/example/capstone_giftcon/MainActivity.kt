package com.example.capstone_giftcon

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.capstone_giftcon.adapter.GiftRecyclerAdapter
import com.example.capstone_giftcon.database.DbOpenHelper
import com.example.capstone_giftcon.databinding.ActivityMainBinding
import com.example.capstone_giftcon.ui.viewModel.GifticonViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: GifticonViewModel by lazy {
        ViewModelProvider(this).get(GifticonViewModel::class.java)
    }
    private var mDbOpenHelper: DbOpenHelper? = null
    private lateinit var giftRecyclerAdapter: GiftRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        giftRecyclerAdapter = GiftRecyclerAdapter { item ->
            val intent = Intent(this, DetailActivity::class.java)
            with(intent){
                putExtra("giftName", item.giftName);
                putExtra("barcode", item.barcode);
                putExtra("place", item.brand);
                putExtra("expiration", item.expiration);
                putExtra("category", item.category);
                putExtra("imgId", item.imgId);
                putExtra("giftId", item.giftId);
                putExtra("available", item.available);
            }
            overridePendingTransition(0, 0);
            startActivity(intent);
        }
        binding.recyclerView.adapter = giftRecyclerAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

//        viewModel.giftList.observe(this){ items ->
//            items.let {
//                giftRecyclerAdapter.submitList(it)
//            }
//        }

        mDbOpenHelper = DbOpenHelper(this)
        mDbOpenHelper!!.open()

        //toolbar 관련
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        binding.navigationView.setNavigationItemSelectedListener(this as NavigationView.OnNavigationItemSelectedListener)

        val categoryItems = arrayOf("카테고리 ", "카페/음료 ", "편의점", "영화", "음식점", "기타")

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.spinner_text, categoryItems
        )
        binding.spinner.setPadding(0, 0, 0, 0)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 드롭다운 클릭 시 선택 창
        binding.spinner.adapter = spinnerAdapter // 스피너에 어댑터 설정

        //스피너에서 선택 했을 경우 이벤트 처리
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                binding.textCategory.text = categoryItems[position]
                if (position != 0) {
                    setView(position, true)
                } else if (position == 0) {
                    setView(null, true)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.textCategory.text = "선택 : "
            }
        }
        var available = true // 사용 가능
        binding.availableLabel.setOnClickListener(View.OnClickListener {
            if (!available) { // 홀수
                binding.availableText.text = "사용완료된 기프티콘 총 "
                binding.giftNum.setTextColor(Color.parseColor("#E44A1A"))
                setView(null, false)
            } else {
                binding.availableText.text = "사용가능한 기프티콘 총 "
                binding.giftNum.setTextColor(resources.getColor(R.color.black))
                setView(null, true)
            }
            available = !available
        })

        binding.mainBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
    }

    fun setView(data: Int?, tf: Boolean) {
        viewModel.getGiftList(data, tf)
        viewModel.getGiftResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    viewModel.giftList.observe(this){ list ->
                        giftRecyclerAdapter.submitList(list)
                    }
                    viewModel.giftListSize.observe(this){ size ->
                        binding.giftNum.text = size
                    }
                }
                NetworkResult.FAIL -> {
                    Log.d("setList : onFailure", "")
                }
                else -> {
                    Log.d("setList : NotSuccessful", "response is not successful")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                val intent =
                    Intent(applicationContext, GiftAddActivity::class.java) // 기프티콘 등록 창으로 화면전환
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item1 -> {
                val intent = Intent(applicationContext, MypageActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.menu_item2 -> {
                val intent = Intent(applicationContext, BoardActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            R.id.menu_item4 -> {
                val intent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() { //뒤로가기 했을 때
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed();
            ActivityCompat.finishAffinity(this@MainActivity)
        }
    }
}