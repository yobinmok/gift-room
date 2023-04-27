package com.example.capstone_giftcon.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.repository.SignRepository
import com.example.capstone_giftcon.repository.UserRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class UserViewModel: ViewModel() {

    private val repository: UserRepository by lazy {
        UserRepository()
    }

    private val signRepository: SignRepository by lazy {
        SignRepository()
    }

    val getProfileResult: LiveData<NetworkResult>
        get() = repository.getProfileResult

    val postProfileResult: LiveData<NetworkResult>
        get() = repository.postProfileResult

    val postProfileImgResult: LiveData<NetworkResult>
        get() = repository.postProfileImgResult

    val duplicateNameResult: LiveData<NetworkResult>
        get() = signRepository.duplicateNameResult

    val checkPwdResult: LiveData<NetworkResult>
        get() = repository.checkPwdResult

    val deleteAccountResult: LiveData<NetworkResult>
        get() = repository.deleteAccountResult

    val getPushResult: LiveData<NetworkResult>
        get() = repository.getPushResult

    val postPushResult: LiveData<NetworkResult>
        get() = repository.postPushResult

    val profile: LiveData<HashMap<String, String>>
        get() = repository.profile

    val timeSpan: LiveData<JsonArray>
        get() = repository.timeSpan

    fun getProfile(){
        repository.getProfile()
    }

    fun postProfile(data: JsonObject){
        // 닉네임, 비밀번호 변경에 사용
        repository.postProfile(data)
    }

    fun postProfileImg(url: String){
        repository.postProfileImg(url)
    }

    fun deleteAccount(){
        repository.deleteAccount()
    }

    fun duplicateNameCheck(data: String){
        signRepository.checkNameDuplicate(data)
    }

    fun checkPwd(data: JsonObject){
        repository.checkPwd(data)
    }

    fun getPush(){
        repository.getPush()
    }

    fun postPush(data: JsonObject){
        repository.postPush(data)
    }
}