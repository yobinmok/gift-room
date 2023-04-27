package com.example.capstone_giftcon.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.capstone_giftcon.NetworkResult
import com.example.capstone_giftcon.dto.GiftResponse
import com.example.capstone_giftcon.dto.ImgResponse
import com.example.capstone_giftcon.item.GiftItem
import com.example.capstone_giftcon.repository.GifticonRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.UnsupportedEncodingException

class GifticonViewModel: ViewModel() {

    private val repository: GifticonRepository by lazy {
        GifticonRepository()
    }

    val getGiftResult: LiveData<NetworkResult>
        get() = repository.getGiftResult

    val postGiftResult: LiveData<NetworkResult>
        get() = repository.postGiftResult

    val postPhotoResult: LiveData<NetworkResult>
        get() = repository.postPhotoResult

    val updateGiftResult: LiveData<NetworkResult>
        get() = repository.updateGiftResult

    val deleteGiftResult: LiveData<NetworkResult>
        get() = repository.deleteGiftResult

    val giftList: LiveData<List<GiftItem>>
        get() = repository.giftList

    val imgResponse: LiveData<ImgResponse>
        get() = repository.imgResponse

    val giftListSize: LiveData<String> get() = repository.giftListSize

    fun getGiftList(data: Int?, available: Boolean){
        repository.getGiftList(data, available)
    }

    fun postPhoto(url: String){
        repository.postPhoto(url)
    }

    fun postGift(data: GiftResponse){
        repository.postGift(data)
    }

    fun updateGift(giftId: Int, data: GiftResponse){
        repository.updateGift(giftId, data)
    }

    fun deleteGift(giftId: Int){
        repository.deleteGift(giftId)
    }
}