package com.griotlegacy.mobile.app.ux.startup
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.model.base.BaseActivity
import com.griotlegacy.mobile.app.ui.theme.GriotLegacyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartupActivity : BaseActivity<StartupViewModel>() {
    override val viewModel: StartupViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isComeFor = intent.getStringExtra(Constants.IS_COME_FOR) ?: Constants.AppScreen.START_UP

        val bundle = intent.extras
        val reset = intent.getStringExtra(Constants.BundleKey.RESET)
        fitSystemWindow(true)
        //installSplashScreen()
        // enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                GriotLegacyTheme {
                    StartupScreen(
                        startDestination = isComeFor,
                        bundle = bundle ?: Bundle.EMPTY,
                        restartApp = reset ?: ""
                    )
                }
            }
            /* GriotLegacyTheme {
                 StartupScreen(startDestination = isComeFor)
             }*/
        }


    }

    override fun onStartup() {
        super.onStartup()
        viewModel.startup()
    }

}
