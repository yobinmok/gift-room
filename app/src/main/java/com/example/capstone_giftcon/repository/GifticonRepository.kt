package com.example.capstone_giftcon.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.database.DbOpenHelper
import com.example.capstone_giftcon.dto.GiftResponse
import com.example.capstone_giftcon.dto.ImgResponse
import com.example.capstone_giftcon.item.GiftItem
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
import java.io.UnsupportedEncodingException

class GifticonRepository {

    private val service = RetrofitApi.retrofitService

    private lateinit var _getGiftResult: MutableLiveData<NetworkResult>
    val getGiftResult: LiveData<NetworkResult> get() = _getGiftResult

    private lateinit var _postPhotoResult: MutableLiveData<NetworkResult>
    val postPhotoResult: LiveData<NetworkResult> get() = _postPhotoResult

    private lateinit var _postGiftResult: MutableLiveData<NetworkResult>
    val postGiftResult: LiveData<NetworkResult> get() = _postGiftResult

    private lateinit var _updateGiftResult: MutableLiveData<NetworkResult>
    val updateGiftResult: LiveData<NetworkResult> get() = _updateGiftResult

    private lateinit var _deleteGiftResult: MutableLiveData<NetworkResult>
    val deleteGiftResult: LiveData<NetworkResult> get() = _deleteGiftResult

    private lateinit var _giftList: MutableLiveData<List<GiftItem>>
    val giftList: LiveData<List<GiftItem>> get() = _giftList

    private lateinit var _giftListSize: MutableLiveData<String>
    val giftListSize: LiveData<String> get() = _giftListSize

    fun getGiftList(data: Int?, available: Boolean){
        service.getGiftList(Utils.getAccessToken(""), data, available).enqueue(object: Callback<List<GiftResponse>>{
            override fun onResponse(
                call: Call<List<GiftResponse>>,
                response: Response<List<GiftResponse>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    val resultList = response.body()!!
                    _giftListSize.value = resultList.size.toString()
                    val giftListTemp: MutableList<GiftItem> = mutableListOf()
                    for(i in resultList.indices){
                        giftListTemp.add(GiftItem(
                            resultList[i].giftName,
                            resultList[i].brand,
                            resultList[i].barcode,
                            resultList[i].expiration,
                            resultList[i].category,
                            resultList[i].imgId, // 이미지 url로 받기
                            resultList[i].giftId,
                            resultList[i].available
                        ))
                    }
                    _giftList.value = giftListTemp.toList()
                    _getGiftResult.value = NetworkResult.SUCCESS
                }else
                    _getGiftResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<List<GiftResponse>>, t: Throwable) {
                _getGiftResult.value = NetworkResult.FAIL
            }
        })
    }

    private lateinit var _imgResponse: MutableLiveData<ImgResponse>
    val imgResponse: LiveData<ImgResponse> get() = _imgResponse

    fun postPhoto(url: String){
        val file = File(url)
        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            file
        ) // 가져온 File 객체를 RequestBody 객체로 변환

        try {
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            service.postPhoto(body).enqueue(object:Callback<ImgResponse>{
                override fun onResponse(call: Call<ImgResponse>, response: Response<ImgResponse>) {
                    if(response.isSuccessful && response.body() != null){
                        _imgResponse.value = response.body()!!
                        _postPhotoResult.value = NetworkResult.SUCCESS
                    }else
                        _postPhotoResult.value = NetworkResult.NOT_SUCCESS
                }

                override fun onFailure(call: Call<ImgResponse>, t: Throwable) {
                    _postPhotoResult.value = NetworkResult.FAIL
                }
            })
        }catch (e: UnsupportedEncodingException){
            e.printStackTrace()
        }
    }

    fun postGift(data: GiftResponse){
        service.postGift(Utils.getAccessToken(""), data).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _postGiftResult.value = NetworkResult.SUCCESS
                }else
                    _postGiftResult.value = NetworkResult.SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _postGiftResult.value = NetworkResult.SUCCESS
            }
        })
    }


    fun updateGift(giftId: Int, data: GiftResponse){
        service.updateGift(Utils.getAccessToken(""),giftId, data).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _updateGiftResult.value = NetworkResult.SUCCESS
                }else
                    _updateGiftResult.value = NetworkResult.SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _updateGiftResult.value = NetworkResult.SUCCESS
            }
        })
    }

    fun deleteGift(giftId: Int){
        service.deleteGift(Utils.getAccessToken(""), giftId).enqueue(object:Callback <JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _deleteGiftResult.value = NetworkResult.SUCCESS
                }else
                    _deleteGiftResult.value = NetworkResult.SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _deleteGiftResult.value = NetworkResult.SUCCESS
            }
        })
    }
}