package com.example.capstone_giftcon.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.dto.ImgResponse
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.UnsupportedEncodingException

class UserRepository {

    private val service = RetrofitApi.retrofitService

    private lateinit var _getProfileResult: MutableLiveData<NetworkResult>
    val getProfileResult: LiveData<NetworkResult> get() = _getProfileResult

    private lateinit var _postProfileResult: MutableLiveData<NetworkResult>
    val postProfileResult: LiveData<NetworkResult> get() = _postProfileResult

    private lateinit var _postProfileImgResult: MutableLiveData<NetworkResult>
    val postProfileImgResult: LiveData<NetworkResult> get() = _postProfileImgResult

    private lateinit var _deleteAccountResult: MutableLiveData<NetworkResult>
    val deleteAccountResult: LiveData<NetworkResult> get() = _deleteAccountResult

    private lateinit var _checkPwdResult: MutableLiveData<NetworkResult>
    val checkPwdResult: LiveData<NetworkResult> get() = _checkPwdResult

    private lateinit var _postPushResult: MutableLiveData<NetworkResult>
    val postPushResult: LiveData<NetworkResult> get() = _postPushResult

    private lateinit var _getPushResult: MutableLiveData<NetworkResult>
    val getPushResult: LiveData<NetworkResult> get() = _getPushResult

    private lateinit var _profile: MutableLiveData<HashMap<String, String>>
    val profile: LiveData<HashMap<String, String>> get() = _profile

    private lateinit var _timeSpan: MutableLiveData<JsonArray>
    val timeSpan: LiveData<JsonArray> get() = _timeSpan

    fun getProfile(){
        service.getProfile(Utils.getAccessToken("")).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    val tempHashMap = HashMap<String, String>()
                    tempHashMap["image"] = response.body()!!.get("image").asString
                    tempHashMap["nickname"] = response.body()!!.get("nickname").asString
                    _profile.value = tempHashMap
                    _getProfileResult.value = NetworkResult.SUCCESS
                }else
                    _getProfileResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _getProfileResult.value = NetworkResult.FAIL
            }
        })
    }

    fun postProfile(data: JsonObject){
        service.postProfile(Utils.getAccessToken(""), data).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    if (response.code() == 200)
                        _postProfileResult.value = NetworkResult.SUCCESS
                    else if (response.code() >= 400)
                        _postProfileResult.value = NetworkResult.ERROR
                }else
                    _postProfileResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _postProfileResult.value = NetworkResult.FAIL
            }
        })
    }

    fun postProfileImg(url: String){
        val file = File(url)
        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            file
        ) // 가져온 File 객체를 RequestBody 객체로 변환

        try {
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            service.postProfileImg(Utils.getAccessToken(""), body).enqueue(object:Callback<JsonObject>{
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if(response.isSuccessful && response.body() != null){
                        _postProfileImgResult.value = NetworkResult.SUCCESS
                    }else
                        _postProfileImgResult.value = NetworkResult.NOT_SUCCESS
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    _postProfileImgResult.value = NetworkResult.FAIL
                }
            })
        }catch (e: UnsupportedEncodingException){
            e.printStackTrace()
        }
    }

    fun deleteAccount(){
        service.deleteAccount(Utils.getAccessToken("")).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _deleteAccountResult.value = NetworkResult.SUCCESS
                }else
                    _deleteAccountResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _deleteAccountResult.value = NetworkResult.FAIL
            }
        })
    }

    fun checkPwd(data: JsonObject){
        service.checkPwd(Utils.getAccessToken(""), true, data).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    if(response.code() == 200)
                        _checkPwdResult.value = NetworkResult.SUCCESS
                    else
                        _checkPwdResult.value = NetworkResult.ERROR
                }else
                    _checkPwdResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _checkPwdResult.value = NetworkResult.FAIL
            }
        })
    }

    fun postPush(data: JsonObject){
        service.postPush(Utils.getAccessToken(""), data).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful)
                    _postPushResult.value = NetworkResult.SUCCESS
                else
                    _postPushResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _postPushResult.value = NetworkResult.FAIL
            }
        })
    }

    fun getPush(){
        service.getPush(Utils.getAccessToken("")).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    _timeSpan.value = response.body()!!.get("timespan").asJsonArray
                    _getPushResult.value = NetworkResult.SUCCESS
                }
                else
                    _getPushResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _getPushResult.value = NetworkResult.FAIL
            }
        })
    }
}