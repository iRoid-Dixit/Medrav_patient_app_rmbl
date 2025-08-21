package com.medrevpatient.mobile.app.ux.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.medrevpatient.mobile.app.ui.base.BaseActivity
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)
        generateFCMToken()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val startDestination = intent?.getStringExtra(Constants.IntentKeys.START_DESTINATION_FOR_MAIN) ?: ""
        val goalId = intent?.getStringExtra(Constants.IntentKeys.GOAL_ID) ?: ""

        setContent {
            MedrevPatientTheme {
                MainScreen(startDestination = startDestination, goalId = goalId)
            }
        }
    }

    private fun generateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { token ->
                    lifecycleScope.launch {
                        viewModel.registerForPushAPI(token, this@MainActivity)
                    }
                    Timber.d("Firebase Token: $token")
                }
            } else {
                Timber.e("Fetching FCM token failed: ${task.exception?.localizedMessage}")
            }
        }
    }
}
