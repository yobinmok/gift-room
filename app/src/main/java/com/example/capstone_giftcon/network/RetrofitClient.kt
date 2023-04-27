package com.example.capstone_giftcon.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://133.186.153.44:3000"
//    private final static String BASE_URL ="https://bc28-175-195-178-192.ngrok.io";

private val gson = GsonBuilder()
    .setLenient().create()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

object RetrofitApi{
    val retrofitService: ServiceApi by lazy{
        retrofit.create(ServiceApi::class.java)
    }
}

// 다른 방식 참고
//class RetrofitService {
//
//    companion object {
//        //통신할 서버 url
//        private const val baseUrl = "http://12.345.678.910"
//
//        //Retrofit 객체 초기화
//        val retrofit: Retrofit = Retrofit.Builder()
//            .baseUrl(this.baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val test: TestService = retrofit.create(TestService::class.java)
//    }
//}
//
//class RetrofitClient{
//    private var retrofit: Retrofit? = null
//    fun getClient(): Retrofit? {
//        val gson = GsonBuilder()
//            .setLenient().create()
//
//        if(retrofit == null){
//            retrofit = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//        }
//        return retrofit
//    }
//}