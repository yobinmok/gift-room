package com.example.capstone_giftcon.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.dto.JoinData
import com.example.capstone_giftcon.dto.LoginData
import com.example.capstone_giftcon.repository.SignRepository

class SignViewModel: ViewModel() {

    // 카카오토큰, LoginData, testToken -> 비동기로 작업 -> coroutine

    val signRepository: SignRepository by lazy {
        SignRepository()
    }

    val userLoginResult: LiveData<NetworkResult>
        get() =  signRepository.userLoginResult

    val userJoinResult: LiveData<NetworkResult>
        get() =  signRepository.userJoinResult

    val duplicateIdResult: LiveData<NetworkResult>
        get() =  signRepository.duplicateIdResult

    val duplicateNameResult: LiveData<NetworkResult>
        get() =  signRepository.duplicateNameResult


    fun userLogin(data: LoginData){
        signRepository.userLogin(data)
    }

    fun userJoin(data: JoinData){
        signRepository.userJoin(data)
    }

    fun checkIdDuplicate(data: String){
        signRepository.checkIdDuplicate(data)
    }

    fun checkNameDuplicate(data: String){
        signRepository.checkNameDuplicate(data)
    }
}