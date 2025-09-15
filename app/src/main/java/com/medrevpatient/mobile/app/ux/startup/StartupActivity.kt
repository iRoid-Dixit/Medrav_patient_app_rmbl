package com.medrevpatient.mobile.app.ux.startup

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.model.base.BaseActivity
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartupActivity : BaseActivity<StartupViewModel>() {
    override val viewModel: StartupViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isComeFor = intent.getStringExtra(Constants.IS_COME_FOR) ?: Constants.AppScreen.START_UP
        val bundle = intent.extras
        val reset = intent.getStringExtra(Constants.BundleKey.RESET)
        Log.d("TAG", "isComeForm: $isComeFor")
        fitSystemWindow(false)
        enableEdgeToEdge()
        setContent {
            StartupScreen(
                startDestination = isComeFor,
                bundle = bundle ?: Bundle.EMPTY,
                restartApp = reset ?: ""
            )
        }
    }
    override fun onStartup() {
        super.onStartup()
        viewModel.startup()
    }
}
