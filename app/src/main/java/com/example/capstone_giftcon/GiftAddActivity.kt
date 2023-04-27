package com.example.capstone_giftcon

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.capstone_giftcon.database.DbOpenHelper
import com.example.capstone_giftcon.databinding.ActivityGiftAddBinding
import com.example.capstone_giftcon.dto.GiftResponse
import com.example.capstone_giftcon.ui.viewModel.GifticonViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

class GiftAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftAddBinding

    private val viewModel: GifticonViewModel by lazy{
        ViewModelProvider(this).get(GifticonViewModel::class.java)
    }
    private var mDbOpenHelper: DbOpenHelper? = null
    private var giftId = 0
    private var imgId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mDbOpenHelper = DbOpenHelper(this)
        mDbOpenHelper!!.open()
        mDbOpenHelper!!.create()

        // 이미지 첨부
        binding.uploadImage.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        })

        // 카테고리
        val items = arrayOf("카테고리", "카페/음료", "편의점", "영화", "음식점", "기타")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // 드롭다운 클릭 시 선택 창
        binding.spinner.adapter = adapter // 스피너에 어댑터 설정

        // 스피너에서 선택 했을 경우 이벤트 처리
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                binding.textCategory.text = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.textCategory.text = "선택 : "
            }
        }
    }

    private fun giftAdd() { // 기프티콘 등록
        val data = GiftResponse(
            binding.textGiftName.text.toString(),
            binding.textBarcode.text.toString(),
            binding.textPlace.text.toString(),
            binding.textExpiration.text.toString(),
            binding.spinner.selectedItemId.toInt(),
            imgId,
            giftId,
            true
        )

        viewModel.postGift(data)
        viewModel.postGiftResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    Log.d("기프티콘 등록 성공", "성공!")
                    val intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    ) // 기프티콘 등록 창으로 화면전환
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                else -> {
                    Log.d("GiftAddActivity", "기프티콘 등록 실패")
                }
            }
        }
    }

    // 이미지뷰에 선택한 이미지 띄우기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data!!)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    verifyStoragePermissions(this)
                    val url = getPath(data.data)
                    val `in`: InputStream = contentResolver.openInputStream(data.data!!)!!
                    val img: Bitmap = BitmapFactory.decodeStream(`in`) // 이미지를 비트맵 형식으로 가져옴
                    insertImg( img, url) // DB에 이미지 삽입 -> String.valueOf(mDbOpenHelper.selectId())통해 아이디 확인 가능
                    imgId = mDbOpenHelper!!.selectId()
                    `in`.close()
                    binding.uploadImage.setImageBitmap(img) // 이미지뷰에 이미지 띄우기
                    uploadFile(url) // 서버에 이미지 전송
                } catch (e: Exception) {
                    Log.e("upload fail", e.message!!)
                    e.printStackTrace()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    //서버에 이미지 전송 후 OCR 결과 출력
    private fun uploadFile(url: String) {
        binding.textGiftName.hint = "텍스트 인식 중 ..."
        binding.textBarcode.hint = "텍스트 인식 중 ..."
        binding.textPlace.hint = "텍스트 인식 중 ..."
        binding.textExpiration.hint = "텍스트 인식 중 ..."
        viewModel.postPhoto(url)
        viewModel.postPhotoResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    viewModel.imgResponse.observe(this){ response ->
                        binding.textGiftName.setText(response.giftName)
                        binding.textBarcode.setText(response.barcode)
                        binding.textPlace.setText(response.brand)
                        binding.textExpiration.setText(response.expiration)
                    }
                }
                else -> {
                    Log.d("GiftAddActivity", "이미지 등록 실패")
                    Toast.makeText(applicationContext, "사진 선택 취소", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // DB에 이미지 삽입
    private fun insertImg(img: Bitmap, name: String?) {
        val stream = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val icon = stream.toByteArray()
        mDbOpenHelper!!.insertColumn(icon, name)
        Log.d("insert", "insert 성공!")
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //
        menuInflater.inflate(R.menu.menu_register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                giftAdd()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    companion object {
        private const val REQUEST_CODE = 0

        // Storage Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        /**
         * Checks if the app has permission to write to device storage
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity
         */
        fun verifyStoragePermissions(activity: Activity) {
            // Check if we have write permission
            val permission: Int = ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}