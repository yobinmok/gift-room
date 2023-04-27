package com.example.capstone_giftcon.network

import com.example.capstone_giftcon.dto.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

// import com.example.capstone_giftcon.data. ~~ 등의 데이터 임포트
interface ServiceApi {
    // user
    @POST("/user/signin")
    fun userLogin(@Body data: LoginData): Call<LoginResponse>

    @POST("user/signup")
    fun userJoin(@Body data: JoinData): Call<JsonObject>

    @POST("user/auth") // 인증토큰
    fun testToken(@Header("Authorization") token: String?): Call<JsonObject>

    @POST("user/kakao-login")  // 카카오 로그인
    fun kakaoToken(
        @Header("access_token") access_token: String,
        @Header("fcm_token") fcm_token: String
    ): Call<JsonObject>

    @GET("user/check") // 이름, 닉네임 중복제거 합침
    fun duplicateCheck(@Query("id") id: String?,
                       @Query("name") name: String?): Call<JsonObject>

//    @GET("user/check")
//    fun duplicateNameCheck(@Query("nickname") data: String): Call<JsonObject>

    // gifticon
    @GET("/gifticon") // 기프티콘 리스트
    fun getGiftList(
        @Header("Authorization") token: String,
        @Query("g_category") data: Int?,
        @Query("available") available: Boolean
    ): Call<List<GiftResponse>>

    @POST("/gifticon") // 기프티콘 등록 시 최종 데이터 전송
    fun postGift(
        @Header("Authorization") token: String,
        @Body data: GiftResponse
    ): Call<JsonObject>

    @Multipart
    @POST("/gifticon/image")// 이미지 업로드
    fun postPhoto(@Part image: MultipartBody.Part?): Call<ImgResponse>

    @POST("gifticon/{id}") // 기프티콘 정보 수정
    fun updateGift(
        @Header("Authorization") token: String?,
        @Path("id") id: Int,
        @Body data: GiftResponse?
    ): Call<JsonObject>

    @DELETE("gifticon/{id}") // 기프티콘 삭제
    fun deleteGift(
        @Header("Authorization") token: String?,
        @Path("id") id: Int
    ): Call<JsonObject>


    ///////// 0406 Binding 수정

    
    @POST("/place") // 사용처 정보
    fun getSearchKeyword(@Body data: LocationData): Call<List<LocationResponse>>

    // 설정
    @GET("/user/profile") // 프로필(이름, 프사) 받기
    fun getProfile(@Header("Authorization") token: String): Call<JsonObject>

    @POST("/user/profile") // 수정한 사용자 이름 보내기
    fun postProfile(
        @Header("Authorization") token: String,
        @Body data: JsonObject
    ): Call<JsonObject>

    @DELETE("/user/withdrawal")  // 계정 탈퇴
    fun deleteAccount(@Header("Authorization") token: String): Call<JsonObject>

    @POST("/user/profile")// 기존 비밀번호 확인
    fun checkPwd(
        @Header("Authorization") token: String,
        @Query("pw_auth") data: Boolean,
        @Body pwd: JsonObject
    ): Call<JsonObject>

    @Multipart
    @PUT("/user/profile/image") // 프사 등록
    fun postProfileImg(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Call<JsonObject>

    // 푸시 알림
    @GET("/notification")
    fun getPush(@Header("Authorization") token: String): Call<JsonObject>

    @POST("/notification")
    fun postPush(
        @Header("Authorization") token: String,
        @Body data: JsonObject
    ): Call<JsonObject>

    // 게시판
    @GET("transaction-posts") // 게시글 목록
    fun setBoard(@Header("Authorization") token: String): Call<List<BoardResponse>>

    @GET("transaction-posts")// 찜한, 내가 작성한 게시글 목록 합침
    fun setSpecificBoard(
        @Header("Authorization") token: String,
        @Query("my_wish") wishData: Boolean?,
        @Query("my_post") postData: Boolean?
    ): Call<List<BoardResponse>>

    @Multipart
    @POST("transaction-posts")// 게시글 등록
    fun boardAdd(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @PartMap params: HashMap<String, RequestBody>
    ): Call<JsonObject>

    //Call<JsonObject> boardAdd(@Header("Authorization") String token, @PartMap Map<String, RequestBody> params);
    @GET("transaction-posts/{id}")// 게시글 세부사항
    fun boardDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<BoardResponse>

    @DELETE("transaction-posts/{id}")// 게시글 삭제
    fun boardDelete(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<JsonObject>

    @PUT("transaction-posts/{id}")// 게시글 수정
    fun boardUpdate(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body data: JsonObject
    ): Call<JsonObject>

    // 댓글
    @POST("comments")// 댓글 등록
    fun commentAdd(
        @Header("Authorization") token: String,
        @Body data: JsonObject
    ): Call<JsonObject>

    @GET("comments")// 댓글 불러오기
    fun setComment(
        @Header("Authorization") token: String,
        @Query("post_id") id: Int
    ): Call<List<CommentData>>

    @DELETE("comments/{id}")// 댓글 삭제
    fun deleteComment(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<JsonObject>

    @PUT("comments/{id}")// 게시글 수정
    fun updateComment(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body data: JsonObject
    ): Call<JsonObject>

    // 찜하기
    @POST("wishlist/{id}")// 찜하기 등록
    fun wishListAdd(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<JsonObject>

    @DELETE("wishlist/{id}")// 찜하기 삭제
    fun wishListDelete(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<JsonObject>
}