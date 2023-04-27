package com.example.capstone_giftcon.item

import android.graphics.Bitmap

// 리스트 섬네일에 사용하는 변수
data class GiftItem(
    var giftName: String,
    var brand: String,
    var barcode: String,
    var expiration: String,
    var category: Int,
    var imgId: Int, // 이미지 url로 받기 -> 현재는 SQLite DB 내 이미지 아이디를 주고있음
    var giftId: Int,
    var available: Boolean
) {
}