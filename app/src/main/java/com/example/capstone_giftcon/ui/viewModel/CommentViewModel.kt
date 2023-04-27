package com.example.capstone_giftcon.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstone_giftcon.item.CommentItem
import com.example.capstone_giftcon.repository.CommentRepository
import com.google.gson.JsonObject

class CommentViewModel: ViewModel() {

    private val repository: CommentRepository by lazy{
        CommentRepository()
    }

    val commendId: Int? = null
    val mine: Int? = null
    val commentText: String? = null

    private lateinit var _updateBool: MutableLiveData<Boolean>
    val updateBool: LiveData<Boolean> get() = _updateBool!!

    val addCommentResult get() = repository.addCommentResult
    val setCommentResult get() = repository.setCommentResult
    val deleteCommentResult get() = repository.deleteCommentResult
    val updateCommentResult get() = repository.updateCommentResult

    val commentList:LiveData<List<CommentItem>>
        get() = repository.commentList

    val commentCount: Int get() = repository.commentList.value!!.size

    fun updateBoolean(bool: Boolean){
        _updateBool.value = bool
    }
    fun addComment(data: JsonObject){
        repository.addComment(data)
    }

    fun setComment(boardId: Int){
        repository.setComment(boardId)
    }

    fun deleteComment(commentId: Int){
        repository.deleteComment(commentId)
    }

    fun updateComment(commentId: Int, data: JsonObject){
        repository.updateComment(commentId, data)
    }
}