package com.example.capstone_giftcon.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.dto.CommentData
import com.example.capstone_giftcon.item.CommentItem
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.Utils
import com.google.gson.JsonObject
import okhttp3.internal.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentRepository {

    private val service = RetrofitApi.retrofitService

    private lateinit var _addCommentResult: MutableLiveData<NetworkResult>
    val addCommentResult get() = _addCommentResult

    private lateinit var _setCommentResult: MutableLiveData<NetworkResult>
    val setCommentResult get() = _setCommentResult

    private lateinit var _deleteCommentResult: MutableLiveData<NetworkResult>
    val deleteCommentResult get() = _deleteCommentResult

    private lateinit var _updateCommentResult: MutableLiveData<NetworkResult>
    val updateCommentResult get() = _updateCommentResult

    private lateinit var _commentList: MutableLiveData<List<CommentItem>>
    val commentList: LiveData<List<CommentItem>> get() = _commentList

    fun addComment(data: JsonObject){
        service.commentAdd(Utils.getAccessToken(""), data).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _addCommentResult.value = NetworkResult.SUCCESS
                }else
                    _addCommentResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _addCommentResult.value = NetworkResult.FAIL
            }
        })
    }

    fun setComment(boardId: Int){
        service.setComment(Utils.getAccessToken(""), boardId).enqueue(object: Callback<List<CommentData>>{
            override fun onResponse(
                call: Call<List<CommentData>>,
                response: Response<List<CommentData>>
            ) {
                if(response.isSuccessful && response.body() != null){
                    val result = response.body()!!
                    val tempList: MutableList<CommentItem> = mutableListOf()
                    for (i in 0 until response.body()!!.size){
                        tempList.add(CommentItem(
                            result[i].id,
                            result[i].author,
                            result[i].text,
                            result[i].created_at,
                            result[i].my_comment,
                            result[i].author_img
                        ))
                    }
                    _commentList.value = tempList
                    _setCommentResult.value = NetworkResult.SUCCESS
                }else
                    _setCommentResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<List<CommentData>>, t: Throwable) {
                _setCommentResult.value = NetworkResult.FAIL
            }
        })
    }

    fun deleteComment(commentId: Int){
        service.deleteComment(Utils.getAccessToken(""), commentId).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _deleteCommentResult.value = NetworkResult.SUCCESS
                }else
                    _deleteCommentResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _deleteCommentResult.value = NetworkResult.FAIL
            }
        })
    }

    fun updateComment(commentId: Int, data: JsonObject){
        service.updateComment(Utils.getAccessToken(""),commentId, data).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _updateCommentResult.value = NetworkResult.SUCCESS
                }else
                    _updateCommentResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _updateCommentResult.value = NetworkResult.FAIL
            }
        })
    }
}