@file:OptIn(ExperimentalMaterial3Api::class)

package com.medrevpatient.mobile.app.ux.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.model.base.BaseActivity
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel>() {
    companion object {
        fun newIntent(c: Context): Intent {
            val intent = Intent(c, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
    override val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isComeFor =
            intent.getStringExtra(Constants.IS_COME_FOR) ?: Constants.AppScreen.PERSONALIZE_AUDIO
        fitSystemWindow(true)
        Log.d("TAG", "onCreate: $isComeFor")
        setContent {

            MedrevPatientTheme {
                    MainScreen(startDestination = isComeFor)
                }


        }
        generateFCMToken()
    }

    private fun generateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.e("TAG", "FirebaseToken $token")
                viewModel.registerForPushAPI(token, this)
            } else {
                Log.e(
                    "TAG",
                    "Fetching FCM registration token failed ${task.exception?.localizedMessage}"
                )
                return@OnCompleteListener
            }
        })
    }


}