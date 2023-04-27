@file:Suppress("OverrideDeprecatedMigration")

package com.example.capstone_giftcon

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.databinding.ActivityBoardModifyBinding
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.ServiceApi
import com.example.capstone_giftcon.network.Utils.Companion.getAccessToken
import com.example.capstone_giftcon.ui.viewModel.BoardViewModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("OverrideDeprecatedMigration")
class BoardModifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardModifyBinding

    private val viewModel: BoardViewModel by viewModels()
    private var service: ServiceApi = RetrofitApi.retrofitService

    private lateinit var url: String
    private var postId = 0
    private var myPost: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardModifyBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_board_modify)

        setSupportActionBar(binding.myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // BoardDetail로부터 인텐트값 받기
        val intent = intent
        binding.title.setText(intent.getStringExtra("title"))
        binding.author.text = intent.getStringExtra("author")
        binding.date.text = intent.getStringExtra("date")
        binding.contents.setText(intent.getStringExtra("content"))
        Glide.with(this@BoardModifyActivity).load(intent.getStringExtra("img")).into(binding.image)

        postId = intent.getIntExtra("post_id", 0)
        myPost = intent.getBooleanExtra("my_post", java.lang.Boolean.TRUE)
        binding.image.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, REQUEST_CODE)
        })
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
                    binding.image.setImageBitmap(img)
                } catch (e: Exception) {
                    Log.e("upload fail", e.message!!)
                    e.printStackTrace()
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun boardUpdate() {
        val data = JsonObject()
        data.addProperty("title", binding.title.text.toString())
        data.addProperty("content", binding.contents.text.toString())

        viewModel.boardUpdate(postId, data)
        viewModel.boardUpdateResult.observe(this){
            when(it) {
                NetworkResult.SUCCESS -> {
                    val intent = Intent(applicationContext, BoardDetailActivity::class.java)
                    intent.putExtra("id", postId)
                    intent.putExtra("my_post", myPost)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }else ->
                    Toast.makeText(this, "게시글 수정에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
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
                boardUpdate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
    }
}