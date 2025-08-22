package com.medrevpatient.mobile.app.utils.services

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.utils.notification.NotificationUtils
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.main.MainActivity

import kotlin.random.Random

class FireBaseMessageService : FirebaseMessagingService() {
    private val tag = "Messages"
    override fun onNewToken(s: String) {
        //Prefs.putString(Constants.PrefsKeys.FIREBASE_MESSAGE_TOKEN, s)
        Log.e("FirebaseToken", "The token refreshed $s")
        super.onNewToken(s)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val gcmRec = Intent("NotificationReceived")
        gcmRec.putExtra("chatId", remoteMessage.data["groupId"])
        gcmRec.putExtra("count", remoteMessage.data["count"])
        Log.e(tag, "notificationType ${remoteMessage.data}")
        selectPushType(remoteMessage)
    }
    private fun selectPushType(remoteMessage: RemoteMessage) {
        var title = "" //baseContext.getString(R.string.app_name)
        var body = ""// baseContext.getString(R.string.app_name)
        if (remoteMessage.notification?.title != null)
            title = remoteMessage.notification?.title.toString()
        Log.d("TAG", "selectPushType: $title")

        if (remoteMessage.notification?.body != null)
            body = remoteMessage.notification?.body.toString()

        NotificationUtils().sendNotification(
            baseContext,
            title,
            body,
            getNotificationIntent(remoteMessage),
            Random.nextInt(5000)
        )
    }
    private fun getNotificationIntent(remoteMessage: RemoteMessage): Intent {

        try {
            return if (!remoteMessage.data["notificationType"].isNullOrEmpty()) {
                when (remoteMessage.data["notificationType"]?.toInt()) {
                    Constants.NotificationPush.LIKE_DISLIKE_TYPE -> {
                        Log.d(
                            "TAG",
                            "getNotificationIntent:${remoteMessage.data["notificationType"]?.toInt()}"
                        )
                        Log.e("TYPE_OF", "PUSH_EVENT_CREATE")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(
                            Constants.IS_COME_FOR,
                            Constants.AppScreen.MAIN_VILLAGE_SCREEN
                        )
                    }

                    Constants.NotificationPush.COMMENT_TYPE -> {
                        val bundle = Bundle()
                        val intent = Intent(this, ContainerActivity::class.java)
                        intent.putExtra(
                            Constants.IS_COME_FOR,
                            Constants.AppScreen.POST_DETAILS_SCREEN
                        )
                        bundle.putString(Constants.BundleKey.POST_ID, remoteMessage.data["postId"])
                        intent.putExtra(Constants.IS_FORM, bundle)
                    }

                    Constants.NotificationPush.ADD_POST -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(
                            Constants.IS_COME_FOR,
                            Constants.AppScreen.GRIOT_LEGACY_SCREEN
                        )

                    }

                    Constants.NotificationPush.MESSAGE -> {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.MESSAGE_SCREEN)

                    }
                    else -> {
                        MainActivity.newIntent(baseContext)
                    }
                }
            } else {
                MainActivity.newIntent(baseContext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MainActivity.newIntent(baseContext)
        }
    }
}