package com.example.capstone_giftcon.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.dto.BoardResponse
import com.example.capstone_giftcon.item.BoardItem
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.Utils
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.HashMap

class BoardRepository {

    private val service = RetrofitApi.retrofitService

    private lateinit var _setBoardResult: MutableLiveData<NetworkResult>
    val setBoardResult: LiveData<NetworkResult> get() = _setBoardResult

    private lateinit var _boardAddResult: MutableLiveData<NetworkResult>
    val boardAddResult: LiveData<NetworkResult> get() = _boardAddResult

    private lateinit var _boardDetailResult: MutableLiveData<NetworkResult>
    val boardDetailResult: LiveData<NetworkResult> get() = _boardDetailResult

    private lateinit var _boardDeleteResult: MutableLiveData<NetworkResult>
    val boardDeleteResult: LiveData<NetworkResult> get() = _boardDeleteResult

    private lateinit var _boardUpdateResult: MutableLiveData<NetworkResult>
    val boardUpdateResult: LiveData<NetworkResult> get() = _wishDeleteResult

    private lateinit var _wishAddResult: MutableLiveData<NetworkResult>
    val wishAddResult: LiveData<NetworkResult> get() = _wishAddResult

    private lateinit var _wishDeleteResult: MutableLiveData<NetworkResult>
    val wishDeleteResult: LiveData<NetworkResult> get() = _wishDeleteResult

    private lateinit var _boardList: MutableLiveData<List<BoardItem>>
    val boardList: LiveData<List<BoardItem>> get() = _boardList

    private lateinit var _specificBoardList: MutableLiveData<List<BoardItem>>
    val specificBoardList: LiveData<List<BoardItem>> get() = _specificBoardList


    private lateinit var _boardDetail: MutableLiveData<BoardResponse>
    val boardDetail: LiveData<BoardResponse> get() = _boardDetail

    fun setSpecificBoard(wishData: Boolean, postData: Boolean){
        service.setSpecificBoard(Utils.getAccessToken(""), wishData, postData).enqueue(object: Callback<List<BoardResponse>>{
            override fun onResponse(
                call: Call<List<BoardResponse>>,
                response: Response<List<BoardResponse>>
            ) {
                if(response.isSuccessful && response.body() != null){
                    val boardList = response.body()!!
                    val tempList: MutableList<BoardItem> = mutableListOf()
                    for (i in 0 until response.body()!!.size) {
                        tempList.add(
                            BoardItem(
                                boardList[i].title,
                                boardList[i].content,
                                boardList[i].author,
                                boardList[i].created,
                                boardList[i].image,
                                boardList[i].my_post,
                                boardList[i].id
                            )
                        )
                    }
                    _specificBoardList.value = tempList.toList()
                    _setBoardResult.value = NetworkResult.SUCCESS
                }else
                    _setBoardResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<List<BoardResponse>>, t: Throwable) {
                _setBoardResult.value = NetworkResult.FAIL
            }
        })
    }

    fun setBoard(){
        service.setBoard(Utils.getAccessToken("")).enqueue(object: Callback<List<BoardResponse>>{
            override fun onResponse(
                call: Call<List<BoardResponse>>,
                response: Response<List<BoardResponse>>
            ) {
                if(response.isSuccessful && response.body() != null){
                    val boardList = response.body()!!
                    val tempList: MutableList<BoardItem> = mutableListOf()
                    for (i in 0 until response.body()!!.size) {
                        tempList.add(
                            BoardItem(
                                boardList[i].title,
                                boardList[i].content,
                                boardList[i].author,
                                boardList[i].created,
                                boardList[i].image,
                                boardList[i].my_post,
                                boardList[i].id
                            )
                        )
                    }
                    _boardList.value = tempList.toList()
                    _setBoardResult.value = NetworkResult.SUCCESS
                }else
                    _setBoardResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<List<BoardResponse>>, t: Throwable) {
                _setBoardResult.value = NetworkResult.FAIL
            }
        })
    }

    fun boardAdd(url: String, title: String, content: String){
        val file = File(url)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val titleBody = RequestBody.create(MediaType.parse("text/plain"), title)
        val contentBody = RequestBody.create(MediaType.parse("text/plain"), content)

        val requestMap = HashMap<String, RequestBody>()
        requestMap["title"] = titleBody
        requestMap["content"] = contentBody

        service.boardAdd(Utils.getAccessToken(""), body, requestMap).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _boardAddResult.value = NetworkResult.SUCCESS
                }else
                    _boardAddResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _boardAddResult.value = NetworkResult.FAIL
            }
        })
    }

    fun boardDetail(id: Int){
        service.boardDetail(Utils.getAccessToken(""), id).enqueue(object : Callback<BoardResponse>{
            override fun onResponse(call: Call<BoardResponse>, response: Response<BoardResponse>) {
                if(response.isSuccessful && response.body() != null){
                    _boardDetail.value = response.body()
                    _boardDetailResult.value = NetworkResult.SUCCESS
                }else
                    _boardDetailResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<BoardResponse>, t: Throwable) {
                _boardDetailResult.value = NetworkResult.FAIL
            }
        })
    }

    fun boardDelete(postId: Int){
        service.boardDelete(Utils.getAccessToken(""), postId).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _boardDeleteResult.value = NetworkResult.SUCCESS
                }else
                    _boardDeleteResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _boardDeleteResult.value = NetworkResult.FAIL
            }
        })
    }

    fun boardUpdate(postId: Int, data: JsonObject){
        service.boardUpdate(Utils.getAccessToken(""), postId, data).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _boardUpdateResult.value = NetworkResult.SUCCESS
                }else
                    _boardUpdateResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _boardUpdateResult.value = NetworkResult.FAIL
            }
        })
    }

    fun wishListAdd(postId: Int){
        service.wishListAdd(Utils.getAccessToken(""), postId).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _wishAddResult.value = NetworkResult.SUCCESS
                }else
                    _wishAddResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _wishAddResult.value = NetworkResult.FAIL
            }
        })
    }

    fun wishListDelete(postId: Int){
        service.wishListDelete(Utils.getAccessToken(""), postId).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _wishDeleteResult.value = NetworkResult.SUCCESS
                }else
                    _wishDeleteResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _wishDeleteResult.value = NetworkResult.FAIL
            }
        })
    }
}