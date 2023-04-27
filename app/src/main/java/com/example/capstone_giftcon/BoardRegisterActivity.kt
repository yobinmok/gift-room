package com.example.capstone_giftcon

import androidx.appcompat.app.AppCompatActivity
import com.example.capstone_giftcon.network.ServiceApi
import android.os.Bundle
import android.content.Intent
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.LinearLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.example.capstone_giftcon.databinding.ActivityBoardRegisterBinding
import com.example.capstone_giftcon.network.RetrofitApi
import okhttp3.RequestBody
import okhttp3.MultipartBody
import com.google.gson.JsonObject
import com.example.capstone_giftcon.network.Utils
import com.example.capstone_giftcon.ui.viewModel.BoardViewModel
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.util.HashMap

class BoardRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardRegisterBinding

    private val viewModel: BoardViewModel by viewModels()

    private var service: ServiceApi = RetrofitApi.retrofitService

    var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // 이미지 첨부
        binding.image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        startManagingCursor(cursor)
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    // 이미지뷰에 선택한 이미지 띄우기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    GiftAddActivity.verifyStoragePermissions(this)
                    url = getPath(data!!.data)
                    val `in` = contentResolver.openInputStream(data.data!!)
                    val img = BitmapFactory.decodeStream(`in`) // 이미지를 비트맵 형식으로 가져옴
                    `in`!!.close()
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.gravity = Gravity.CENTER
                    binding.image.layoutParams = params
                    binding.image.setImageBitmap(img) // 이미지뷰에 이미지 띄우기
                } catch (e: Exception) {
                    Log.e("upload fail", e.message!!)
                    e.printStackTrace()
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun boardAdd(url: String?) {
        if (url == null)
            Toast.makeText(this, "이미지를 등록해주세요.", Toast.LENGTH_SHORT).show()
        else{
            val title = binding.title.text.toString()
            val content = binding.contents.text.toString()

            viewModel.boardAdd(url, title, content)
            viewModel.boardAddResult.observe(this){
                when(it){
                    NetworkResult.SUCCESS -> {
                        val intent = Intent(
                            applicationContext, BoardActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }else ->
                        Toast.makeText(this, "게시글을 등록하지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                boardAdd(url)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
    }
}