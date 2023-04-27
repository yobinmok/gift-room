package com.example.capstone_giftcon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone_giftcon.adapter.CommentRecyclerAdapter
import com.example.capstone_giftcon.database.DbOpenHelper
import com.example.capstone_giftcon.databinding.ActivityBoardDetailBinding
import com.example.capstone_giftcon.network.Utils.Companion.init
import com.example.capstone_giftcon.ui.dialog.MyCommentDialog
import com.example.capstone_giftcon.ui.viewModel.BoardViewModel
import com.example.capstone_giftcon.ui.viewModel.CommentViewModel
import com.google.gson.JsonObject

class BoardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardDetailBinding
    private val boardViewModel: BoardViewModel by viewModels()
    private val commentViewModel: CommentViewModel by viewModels()

    private var mDbOpenHelper: DbOpenHelper? = null

    private var postId = 0
    private var commentPos = 0
    private var commentId = 0
    private lateinit var inputManager: InputMethodManager
    var myWish: Boolean = false
    var myPost: Boolean? = null
    lateinit var myCommentDialog: MyCommentDialog
    lateinit var imgString: String

    private lateinit var commentRecyclerAdapter: CommentRecyclerAdapter
    private var commentListSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init(applicationContext)

        mDbOpenHelper = DbOpenHelper(this)
        mDbOpenHelper!!.open()

        inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        setSupportActionBar(binding.myToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val intent = intent
        postId = intent.getIntExtra("id", 0)
        myPost = intent.getBooleanExtra("my_post", true)

        boardDetail(postId)

        // 댓글 설정 코드
        commentRecyclerAdapter = CommentRecyclerAdapter { item, position ->
            myCommentDialog = MyCommentDialog(item, supportFragmentManager, binding, position)
            myCommentDialog.show(supportFragmentManager, "myCommentDialog")
        }
        binding.recyclerView.adapter = commentRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        commentViewModel.commentList.observe(this){ item ->
            item.let {
                commentListSize = it.size
                commentRecyclerAdapter.submitList(it)
            }
        }

        setComment()

        commentViewModel.updateBool
        binding.wishlist.setOnClickListener {
            if (myWish)
                wishListDelete()
            else
                wishListAdd() }
        binding.commentAdd.setOnClickListener {
            if (commentViewModel.updateBool.value == true)
                updateComment(commentPos, commentId)
            else addComment()
        }
    }

    private fun setComment() {
        commentViewModel.setComment(postId)
        commentViewModel.setCommentResult.observe(this){

        }
    }

    private fun addComment() {
        val content = binding.comment.text.toString()
        val data = JsonObject()
        data.addProperty("post_id", postId)
        data.addProperty("text", content)

        commentViewModel.addComment(data)
        commentViewModel.addCommentResult.observe(this){
            when(it){
                NetworkResult.SUCCESS ->{
                    setComment()
                    // viewModel로 이어주고 싶은데 Room처럼 데이터가 늘상 있는게 아니니까, 전체 코멘트 리스트를 언제 호출해야될지도 모르겠고..
                    binding.commentCount.text = commentListSize.toString()
                    binding.comment.setText("")
                    inputManager.hideSoftInputFromWindow(binding.comment.windowToken, 0)
                }else ->
                    Toast.makeText(this, "댓글을 등록하지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateComment(comment_pos: Int, comment_id: Int) {
        val content = binding.comment.text.toString()
        val data = JsonObject()
        data.addProperty("text", content)

        commentViewModel.updateComment(comment_id, data)
        commentViewModel.updateCommentResult.observe(this){
            when(it){
                NetworkResult.SUCCESS ->{
                    val currentList = commentRecyclerAdapter.currentList.toMutableList()
                    currentList[comment_pos].b_contents = content
                    commentRecyclerAdapter.submitList(currentList)
                    binding.comment.setText("")
                    commentViewModel.updateBoolean(false)
                    inputManager.hideSoftInputFromWindow(binding.comment.windowToken, 0)
                }else ->
                    Toast.makeText(this, "댓글 수정에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteComment(comment_id: Int, position: Int) {
        // 리스트를 다시 받아오는 것은 데이터 소모가 너무 크다
        // 삭제 request를 보내면 DB에서만 삭제되고, 변경된 item의 position값을 가지고 arr을 화면에서 삭제해줌
        // arr.remove(position) 이용
        commentViewModel.deleteComment(comment_id)
        commentViewModel.deleteCommentResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    val currentList = commentRecyclerAdapter.currentList.toMutableList()
                    currentList.removeAt(position)
                    commentRecyclerAdapter.submitList(currentList)
                    binding.commentCount.text = currentList.size.toString()
                }else ->
                    Toast.makeText(this, "댓글 삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun boardDetail(id: Int) {
        boardViewModel.boardDetail(id)
        boardViewModel.boardDetailResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    boardViewModel.boardDetail.observe(this){ boardItem ->
                        myWish = boardItem.my_wish
                        if (myWish)
                            binding.wishlist.setImageResource(R.drawable.ic_like_filled)
                        binding.title.setText(boardItem.title)
                        binding.contents.setText(boardItem.content)
                        binding.author.text = boardItem.author
                        binding.date.text = boardItem.created
                        binding.wishCount.text = boardItem.wish_count
                        binding.commentCount.text = boardItem.comment_count
                        Glide.with(this@BoardDetailActivity).load(boardItem.image).into(binding.image)
                    }
                }else ->
                    Toast.makeText(this, "게시글 내용을 불러오는 데 실패했습니다.\n 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun boardDelete() {
        boardViewModel.boardDelete(postId)
        boardViewModel.boardDeleteResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    val intent = Intent(
                        applicationContext,
                        BoardActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                } else ->
                    Toast.makeText(this, "게시글 삭제에 실패했습니다.\n 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun wishListAdd() {
        boardViewModel.wishListAdd(postId)
        boardViewModel.wishAddResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    // 바인딩
                    binding.wishlist.setImageResource(R.drawable.ic_like_filled)
                    boardDetail(postId)
                    myWish = true
                } else ->
                    Toast.makeText(this, "위시리스트 추가에 실패했습니다.\n 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun wishListDelete() {
        boardViewModel.wishListDelete(postId)
        boardViewModel.wishDeleteResult.observe(this){
            when(it){
                NetworkResult.SUCCESS -> {
                    // 바인딩
                    binding.wishlist.setImageResource(R.drawable.ic_like)
                    boardDetail(postId)
                    myWish = false
                } else ->
                Toast.makeText(this, "위시리스트 삭제에 실패했습니다.\n 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return if (myPost == true) {
            menuInflater.inflate(R.menu.menu_edit, menu)
            true
        } else false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (myPost == true) {
            when (item.itemId) {
                R.id.menu_delete -> {
                    boardDelete()
                    true
                }
                R.id.menu_edit -> {
                    boardEdit()
                    Toast.makeText(this, "edit button", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } else false
    }

    private fun boardEdit() {
        val intent = Intent(applicationContext, BoardModifyActivity::class.java) // 기프티콘 등록 창으로 화면전환

        // 게시글 관련 데이터
        intent.putExtra("title", binding.title.text.toString())
        intent.putExtra("author", binding.author.text.toString())
        intent.putExtra("date", binding.date.text.toString())
        intent.putExtra("content", binding.contents.text.toString())
        intent.putExtra("img", imgString)

        // 화면 전환 시 서버가 게시글 식별하기 위한 데이터
        intent.putExtra("post_id", postId)
        intent.putExtra("my_post", myPost)
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    override fun onBackPressed() { //뒤로가기 했을 때
        val intent = Intent(applicationContext, BoardActivity::class.java) // 기프티콘 등록 창으로 화면전환
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}