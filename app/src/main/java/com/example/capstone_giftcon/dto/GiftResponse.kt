package com.example.capstone_giftcon.dto

import com.google.gson.annotations.SerializedName

data class GiftResponse(
    @SerializedName("g_name") // 쿠폰명
    var giftName: String,
    @SerializedName("g_barcode") // 바코드 번호
    var barcode: String,
    @SerializedName("g_location") // 사용처
    var brand: String,
    @SerializedName("g_expiration") // 유효기간
    var expiration: String,
    @SerializedName("g_category") // 카테고리
    var category: Int,
    @SerializedName("g_imgId") // 기프티콘 이미지 아이디 -> 대신 이미지 주소로 받기
    var imgId: Int,
    @SerializedName("g_giftId") // 기프티콘 아이디
    var giftId: Int,
    @SerializedName("available") // 사용유무
    var available: Boolean
)