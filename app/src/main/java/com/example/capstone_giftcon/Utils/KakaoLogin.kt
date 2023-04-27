package com.example.capstone_giftcon.Utils

import android.app.Application
import com.kakao.sdk.common.KakaoSdk.init

class KakaoLogin : Application() {
    override fun onCreate() {
        super.onCreate()
        init(this, "088df3cee5b3ab3493e80e1d1d08cca8")
    }
}