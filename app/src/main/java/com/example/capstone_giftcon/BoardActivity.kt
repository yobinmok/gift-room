package com.example.capstone_giftcon

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone_giftcon.adapter.BoardRecyclerAdapter
import com.example.capstone_giftcon.databinding.ActivityBoardBinding
import com.example.capstone_giftcon.ui.viewModel.BoardViewModel
import com.google.android.material.navigation.NavigationView

class BoardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBoardBinding
    private val viewModel: BoardViewModel by viewModels()
    private lateinit var boardRecyclerAdapter: BoardRecyclerAdapter

    private var postData: Boolean = false
    private var wishData: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바, 네비게이션 뷰 위한 기초작업
        setSupportActionBar(binding.myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        binding.navigationView.setNavigationItemSelectedListener(this as NavigationView.OnNavigationItemSelectedListener)

        val intent = intent
        postData = intent.getBooleanExtra("postData", false)
        wishData = intent.getBooleanExtra("wishData", false)

        if (!postData && !wishData) {
            binding.boardLabel.text = "거래 게시판"
            setBoard()
        }else if (postData && !wishData){
            binding.boardLabel.text = "내가 작성한 게시글"
            setSpecificBoard()
        } else{
            binding.boardLabel.text = "내가 찜한 게시글"
            setSpecificBoard()
        }

        boardRecyclerAdapter = BoardRecyclerAdapter { item ->
            val intent = Intent(applicationContext, BoardDetailActivity::class.java)
            intent.putExtra("my_post", item.my_post)
            intent.putExtra("id", item.id)
            overridePendingTransition(0, 0)
            startActivity(intent)
        }
        binding.recyclerView.adapter = boardRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.mainBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun setSpecificBoard() {
        viewModel.setSpecificBoard(wishData = wishData, postData = postData)
        viewModel.setBoardResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    viewModel.specificBoardList.observe(this){ list ->
                        boardRecyclerAdapter.submitList(list)
                    }
                }else ->
                    Toast.makeText(this, "게시글 정보를 불러오는 데 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setBoard(){
        viewModel.setBoard()
        viewModel.setBoardResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    viewModel.boardList.observe(this){ list ->
                        boardRecyclerAdapter.submitList(list)
                    }
                }else ->
                Toast.makeText(this, "게시글 정보를 불러오는 데 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 툴바에 띄우는 메뉴 아이템
    override fun onCreateOptionsMenu(menu: Menu): Boolean { //
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.menu_add -> {
                val intent = Intent(
                        applicationContext,
                        BoardRegisterActivity::class.java) // 기프티콘 등록 창으로 화면전환
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() { //뒤로가기 했을 때
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean { // 네비게이션 뷰 내 메뉴 선택 시 화면 이동
        val id = item.itemId
        if (id == R.id.menu_item1) {
            val intent = Intent(applicationContext, MypageActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (id == R.id.menu_item4) {
            val intent = Intent(applicationContext, SettingActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}