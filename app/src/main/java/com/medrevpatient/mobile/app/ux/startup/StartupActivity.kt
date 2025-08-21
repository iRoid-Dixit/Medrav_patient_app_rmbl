package com.medrevpatient.mobile.app.ux.startup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.base.BaseActivity
import com.medrevpatient.mobile.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        val startScreen = intent.getStringExtra(Constants.IntentKeys.NEED_TO_OPEN)
        val bundle = intent.extras

        setContent {
            MedrevPatientTheme {
                StartupScreen(startDestination = startScreen ?: "", bundle = bundle )
            }
        }
    }
}