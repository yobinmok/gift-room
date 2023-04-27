package com.example.capstone_giftcon.dto

import com.google.gson.annotations.SerializedName

data class BoardResponse(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("content")
    var content: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("created_at")
    var created: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("wish_count")
    var wish_count: String,
    @SerializedName("my_post")
    var my_post: Boolean,
    @SerializedName("comment_count")
    var comment_count: String,
    @SerializedName("my_wish")
    var my_wish: Boolean
)