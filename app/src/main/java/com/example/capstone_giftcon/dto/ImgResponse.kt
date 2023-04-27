package com.example.capstone_giftcon.dto

import com.google.gson.annotations.SerializedName

data class ImgResponse(
    @field:SerializedName("g_id")
    var result: Int,
    @SerializedName("g_name")
    var giftName: String,
    @SerializedName("g_location")
    var brand: String,
    @SerializedName("g_barcode")
    var barcode: String,
    @SerializedName("g_expiration")
    var expiration: String
)







