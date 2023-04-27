package com.example.capstone_giftcon.dto

import com.google.gson.annotations.SerializedName

data class JoinData(
    @field:SerializedName("nickname") private val userName: String,
    @field:SerializedName("id") private val userID: String,
    @field:SerializedName("password") private val userPwd: String,
    @field:SerializedName("fcm_token") private val token: String
)

//public class JoinData {
//
//    @SerializedName("nickname")
//    private String userName;
//
//    @SerializedName("id")
//    private String userID;
//
//    @SerializedName("password")
//    private String userPwd;
//
//    @SerializedName("fcm_token")
//    private String token;
//
//    public JoinData(String userName, String userID, String userPwd, String token) {
//        this.userName = userName;
//        this.userID = userID;
//        this.userPwd = userPwd;
//        this.token = token;
//    }
//}