package com.example.capstone_giftcon.Utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.capstone_giftcon.MainActivity
import com.example.capstone_giftcon.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    private var msg: String? = null
    private var title: String? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "onMessageReceived")
        title = remoteMessage.notification!!.title
        msg = remoteMessage.notification!!.body
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val contentIntent =
            PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val mBuilder = NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1, 1000))
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mBuilder.build())
        mBuilder.setContentIntent(contentIntent)
    } //    @Override

    //    public void onNewToken(String token) {
    //        super.onNewToken(token);
    //        Log.e("Refreshed token:",token);
    //    }
    companion object {
        private const val TAG = "FirebaseMsgService"
    }
}