package com.example.capstone_giftcon.dto

import com.google.gson.annotations.SerializedName

//class LoginData(
//    @field:SerializedName("id") var userID: String, @field:SerializedName(
//        "password"
//    ) var userPwd: String
//)

data class LoginData(
    @SerializedName("id")
    val userId: String,
    @SerializedName("password")
    val userPwd: String
)

//public class LoginData {
//    @SerializedName("id")
//    String userID;
//
//    @SerializedName("password")
//    String userPwd;
//
//    public LoginData(String userID, String userPwd) {
//        this.userID = userID;
//        this.userPwd = userPwd;
//    }
//}