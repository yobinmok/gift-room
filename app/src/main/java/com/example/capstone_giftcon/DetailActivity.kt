package com.example.capstone_giftcon

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.capstone_giftcon.database.DbOpenHelper
import com.example.capstone_giftcon.databinding.ActivityDetailBinding
import com.example.capstone_giftcon.dto.GiftResponse
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.ServiceApi
import com.example.capstone_giftcon.ui.viewModel.GifticonViewModel
import java.io.ByteArrayOutputStream

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel: GifticonViewModel by lazy {
        ViewModelProvider(this)[GifticonViewModel::class.java]
    }
    private var mDbOpenHelper: DbOpenHelper? = null
    private var giftId = 0
    private var imgId = 0

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDbOpenHelper = DbOpenHelper(this)
        mDbOpenHelper!!.open()

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val categoryItems = arrayOf("카테고리", "카페/음료 ", "편의점", "영화", "음식점", "기타")
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, categoryItems
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 드롭다운 클릭 시 선택 창
        binding.spinner.adapter = spinnerAdapter // 스피너에 어댑터 설정
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                binding.textCategory.text = categoryItems[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.textCategory.text = "선택 : "
            }
        }

        val intent: Intent = intent
        val keyword = intent.getStringExtra("place").toString()
        with(binding){
            giftName.setText(intent.getStringExtra("giftName"))
            barcode.setText(intent.getStringExtra("barcode"))
            usedPlace.setText(keyword)
            expiration.setText(intent.getStringExtra("expiration"))
            spinner.setSelection(intent.getIntExtra("category", 1))
            spinner.isEnabled = false
            checkBox.isChecked = !intent.getBooleanExtra("available", true)
        }

        imgId = intent.getIntExtra("imgId", 1)
        giftId = intent.getIntExtra("giftId", 1)
        val bitmap: Bitmap = getImg(mDbOpenHelper!!.select(imgId))
        binding.uploadImage.setImageBitmap(bitmap)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        binding.mapButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java) // 기프티콘 등록 창으로 화면전환
            intent.putExtra("keyword", keyword)
            startActivity(intent)
            overridePendingTransition(0, 0)
        })
        binding.checkBox.setOnClickListener {
            giftUpdate()
        }
        binding.editBtn.setOnClickListener {
            giftUpdate()
        }
    }

    private fun getImg(b: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(b, 0, b.size)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                giftDelete()
                true
            }
            R.id.menu_edit -> {
                editMode()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editMode() {
        with(binding){
            giftName.isFocusableInTouchMode = true
            giftName.setBackgroundResource(R.drawable.border)
            barcode.isFocusableInTouchMode = true
            barcode.setBackgroundResource(R.drawable.border)
            usedPlace.isFocusableInTouchMode = true
            usedPlace.setBackgroundResource(R.drawable.border)
            expiration.isFocusableInTouchMode = true
            expiration.setBackgroundResource(R.drawable.border)
            spinner.isEnabled = true
            editBtn.visibility = View.VISIBLE
        }
    }

    private fun giftDelete() {
        viewModel.deleteGift(giftId)
        viewModel.deleteGiftResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    Log.d("기프티콘 삭제 성공", "성공!")
                    val intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                else -> {
                    Log.d("기프티콘 삭제 실패", "실패!")
                }
            }
        }
    }

    private fun giftUpdate() {
        val data = GiftResponse(
            binding.giftName.text.toString(),
            binding.barcode.text.toString(),
            binding.usedPlace.text.toString(),
            binding.expiration.text.toString(),
            binding.spinner.selectedItemId.toInt(),
            imgId,
            giftId,
            !binding.checkBox.isChecked
        )
        viewModel.updateGift(giftId, data)
        viewModel.updateGiftResult.observe(this){
            when(it){
                NetworkResult.SUCCESS ->{
                    Log.d("기프티콘 업데이트 성공", "성공!")
                    val intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                else -> {
                    Log.d("기프티콘 업데이트 실패", "실패!")
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }
}