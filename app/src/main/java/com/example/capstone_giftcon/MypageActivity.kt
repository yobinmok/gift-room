package com.example.capstone_giftcon

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.databinding.ActivityMypageBinding
import com.example.capstone_giftcon.ui.viewModel.UserViewModel
import com.google.android.material.navigation.NavigationView
import java.io.InputStream

class MypageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMypageBinding

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(this)[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)

        // activity 생성 시 기본 값 -> 툴바, 네비게이션 드로어
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        binding.navigationView.setNavigationItemSelectedListener(this as NavigationView.OnNavigationItemSelectedListener)
        val mainBtn: TextView = findViewById(R.id.mainBtn)
        mainBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
        binding.myWish.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, BoardActivity::class.java)
            intent.putExtra("wishData", true)
            intent.putExtra("postData",false)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
        binding.myPost.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, BoardActivity::class.java)
            intent.putExtra("wishData", false)
            intent.putExtra("postData",true)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })

        getProfile() // 사용자 이름 불러오기

        val profile: TextView = findViewById(R.id.profile)
        profile.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        })
        binding.circleProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        })
    }

    private fun postProfileImg(url: String) {
        viewModel.postProfileImg(url)
        viewModel.postProfileImgResult.observe(this) {
            when (it) {
                NetworkResult.SUCCESS -> Log.d("postProfileImg", "Success")
                else -> Log.d("postProfileImg", "Failure / Not success")
            }
        }
    }

    // 프로필 사진 업로드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data!!)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //mDbOpenHelper.deleteAllColumns(); // 이걸 쓸거면 제일 마지막 데이터 호출
                    val url = getPath(data.data)
                    val `in`: InputStream = contentResolver.openInputStream(data.data!!)!!
                    val img: Bitmap = BitmapFactory.decodeStream(`in`)
                    `in`.close()
                    binding.circleProfile.setImageBitmap(img)
                    postProfileImg(url)
                } catch (e: Exception) {
                    Log.e("upload fail", e.message!!)
                    e.printStackTrace()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getProfile(){
        viewModel.getProfile()
        viewModel.getProfileResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    viewModel.profile.observe(this){ hashMap ->
                        binding.userName.text = hashMap["nickname"]
                        Glide.with(this@MypageActivity).load(hashMap["image"]).into(binding.circleProfile)
                    }
                }
                else -> {
                    Toast.makeText(
                        this@MypageActivity,
                        "프로필을 불러올 수 없습니다.",
                        Toast.LENGTH_SHORT).show()
                    Log.d("getProfile : ERROR", "Not Successful or Failure")
                }
            }
        }
    }

    // activity 생성 시 기본 Override 3개
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d("check", GravityCompat.START.toString())
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
                Intent(applicationContext, MainActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_item1) {
            val intent = Intent(applicationContext, MypageActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (id == R.id.menu_item2) {
            val intent = Intent(applicationContext, BoardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else if (id == R.id.menu_item4) {
            val intent =  Intent(applicationContext, SettingActivity::class.java) // 기프티콘 등록 창으로 화면전환
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // 이미지 경로
    private fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        startManagingCursor(cursor)
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    companion object {
        private const val REQUEST_CODE = 0
    }
}