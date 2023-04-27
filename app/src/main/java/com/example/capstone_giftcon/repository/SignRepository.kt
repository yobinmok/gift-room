package com.example.capstone_giftcon.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.dto.JoinData
import com.example.capstone_giftcon.dto.LoginData
import com.example.capstone_giftcon.dto.LoginResponse
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignRepository {

    private val service = RetrofitApi.retrofitService

    private lateinit var _userLoginResult: MutableLiveData<NetworkResult>
    val userLoginResult:LiveData<NetworkResult> get() = _userLoginResult

    private lateinit var _userJoinResult: MutableLiveData<NetworkResult>
    val userJoinResult:LiveData<NetworkResult> get() = _userJoinResult

    private lateinit var _duplicateIdResult: MutableLiveData<NetworkResult>
    val duplicateIdResult:LiveData<NetworkResult> get() = _duplicateIdResult

    private lateinit var _duplicateNameResult: MutableLiveData<NetworkResult>
    val duplicateNameResult:LiveData<NetworkResult> get() = _duplicateNameResult

    fun userLogin(data: LoginData){
        val response = service.userLogin(data)
        _userLoginResult.value = NetworkResult.LOADING
        response.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful && response.body() != null){
                    val token = response.body()!!.token
                    checkToken(token)
                    Utils.setAccessToken(token)
                    _userLoginResult.value = NetworkResult.SUCCESS
                } else{
                    _userLoginResult.value = NetworkResult.NOT_SUCCESS
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _userLoginResult.value = NetworkResult.FAIL
            }
        })
    }

    fun checkToken(token: String){
        service.testToken(token).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    _userLoginResult.value = NetworkResult.SUCCESS
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _userLoginResult.value = NetworkResult.FAIL
            }
        })
    }

    fun userJoin(data: JoinData){
        service.userJoin(data).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    val code = response.code()
                    if (code == 200)
                        _userJoinResult.value = NetworkResult.SUCCESS
                    else if (code == 409)
                        _userJoinResult.value = NetworkResult.FAIL
                }else
                    _userJoinResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _userJoinResult.value = NetworkResult.FAIL
            }

        })
    }

    fun checkIdDuplicate(data: String){
        service.duplicateCheck(data, null).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    val code = response.code()
                    if (code == 200)
                        _duplicateIdResult.value = NetworkResult.SUCCESS
                    else
                        _duplicateIdResult.value = NetworkResult.ERROR
                }else
                    _duplicateIdResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _duplicateIdResult.value = NetworkResult.FAIL
            }
        })
    }

    fun checkNameDuplicate(data: String){
        service.duplicateCheck(null, data).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful && response.body() != null){
                    val code = response.code()
                    if (code == 200)
                        _duplicateNameResult.value = NetworkResult.SUCCESS
                    else if (code == 409)
                        _duplicateNameResult.value = NetworkResult.ERROR
                }else
                    _duplicateNameResult.value = NetworkResult.NOT_SUCCESS
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _duplicateNameResult.value = NetworkResult.FAIL
            }
        })
    }
}