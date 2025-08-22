
package com.medrevpatient.mobile.app.ux.main.videoLoad
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.model.base.BaseActivity
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageVideoPlayerActivity : BaseActivity<ImageVideoPlayerViewModel>() {

    override val viewModel: ImageVideoPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fitSystemWindow(false)
        enableEdgeToEdge()
        val videoLink = intent.getStringExtra(Constants.Values.VIDEO_LINK)

        viewModel.handleIntent(videoLink ?: "")

        setContent {
            MedrevPatientTheme {
                ImageVideoPlayerScreen(viewModel = viewModel)
            }
        }
    }
}