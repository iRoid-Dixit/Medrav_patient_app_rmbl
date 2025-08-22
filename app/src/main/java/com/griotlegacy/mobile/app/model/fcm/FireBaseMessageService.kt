package com.griotlegacy.mobile.app.model.fcm

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.touchlab.kermit.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.utils.notification.NotificationUtils
import kotlin.random.Random

class FireBaseMessageService : FirebaseMessagingService() {
    private val tag = "Messages"
    override fun onNewToken(s: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            appPreferenceDataStore.saveFCMToken(s)
//        }
        super.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val gcmRec = Intent("NotificationReceived")
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(gcmRec)
        selectPushType(remoteMessage)
    }

    private fun selectPushType(remoteMessage: RemoteMessage) {
        Logger.e("notification remoteMessage: $remoteMessage")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.notification?.title ?: baseContext.getString(R.string.app_name)
            val body = remoteMessage.notification?.body ?: baseContext.getString(R.string.app_name)

            NotificationUtils().sendNotification(
                baseContext,
                title,
                body,
                null,
                Random.nextInt(5000)
            )
        }
    }

    /*private fun getNotificationIntent(): Intent {

        val intent = Intent(baseContext, ContainerActivity::class.java)
        intent.putExtra(Constants.BundleKey.EXTRA_BUNDLE, bundle)

        return intent
    }*/
}