package com.example.capstone_giftcon.dto

data class LocationData(
    val keyword: String, // longitude
    val x: Double, // latitude
    val y: Double,
    val radius: Int = 1000
)

//public class LocationData {
//    @SerializedName("keyword")
//    String keyword;
//    @SerializedName("x")
//    Double x; // longitude
//    @SerializedName("y")
//    Double y; // latitude
//    @SerializedName("radius")
//    int radius;
//
//    public LocationData(String keyword, Double x, Double y){
//        this.keyword = keyword;
//        this.x = x;
//        this.y = y;
//        this.radius = 1000;
//    }
//}