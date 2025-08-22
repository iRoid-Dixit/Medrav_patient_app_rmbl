
package com.griotlegacy.mobile.app.ux.main.videoLoad
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.model.base.BaseActivity
import com.griotlegacy.mobile.app.ui.theme.GriotLegacyTheme
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
            GriotLegacyTheme {
                ImageVideoPlayerScreen(viewModel = viewModel)
            }
        }
    }
}