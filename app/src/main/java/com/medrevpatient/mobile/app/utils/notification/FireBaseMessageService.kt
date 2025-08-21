package com.medrevpatient.mobile.app.utils.notification

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.main.MainActivity
import timber.log.Timber
import kotlin.random.Random

class FireBaseMessageService : FirebaseMessagingService() {
    private val tag = "Messages"
    override fun onNewToken(s: String) {
        //Prefs.putString(Constants.PrefsKeys.FIREBASE_MESSAGE_TOKEN, s)
        Timber.e("FirebaseToken", "The token refreshed $s")
        super.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val gcmRec = Intent("NotificationReceived")
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(gcmRec)
        selectPushType(remoteMessage)
    }

    private fun selectPushType(remoteMessage: RemoteMessage) {
        var title = ""
        var body = ""

        if (remoteMessage.data["title"] != null)
            title = remoteMessage.data["title"].toString()

        if (remoteMessage.data["body"] != null)
            body = remoteMessage.data["body"].toString()


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
            return if (remoteMessage.data["type"]?.isNotEmpty() == true) {
                when (remoteMessage.data["type"]) {
                    Constants.NotificationConstants.PUSH_TYPE_PROGRAM -> {
                        Intent(this, MainActivity::class.java).apply {
                            putExtra(Constants.IntentKeys.START_DESTINATION_FOR_MAIN, Constants.Keywords.ALL_PROGRAMS)
                        }
                    }

                    Constants.NotificationConstants.PUSH_TYPE_GOAL -> {
                        Intent(this, MainActivity::class.java).apply {
                            putExtra(Constants.IntentKeys.START_DESTINATION_FOR_MAIN, Constants.Keywords.VIEW_GOALS)
                            remoteMessage.data["goalId"]?.let { putExtra(Constants.IntentKeys.GOAL_ID, it) }
                        }
                    }

                    else -> {
                        MainActivity.newIntent(this)
                    }
                }
            } else {
                MainActivity.newIntent(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MainActivity.newIntent(this)
        }
    }
}