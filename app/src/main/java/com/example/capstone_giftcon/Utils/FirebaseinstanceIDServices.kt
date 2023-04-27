package com.example.capstone_giftcon.Utils

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseinstanceIDServices : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.e(TAG, token!!)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {}

    companion object {
        private const val TAG = "MyFirebaseIIDService"
    }
}