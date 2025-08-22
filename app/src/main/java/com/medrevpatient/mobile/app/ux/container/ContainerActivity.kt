package com.medrevpatient.mobile.app.ux.container

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.model.base.BaseActivity
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class ContainerActivity : BaseActivity<ContainerViewModel>() {
    override val viewModel: ContainerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fitSystemWindow( true)
        //  enableEdgeToEdge()
        val bundle = intent.getBundleExtra(Constants.IS_FORM)
        val isComeFor = intent.getStringExtra(Constants.IS_COME_FOR) ?: Constants.AppScreen.PERSONALIZE_AUDIO
        var mediaList = ""
        var postId = ""
        var url = ""
        var userId = ""
        var messageResponse = ""
        if (bundle != null) {
            userId = bundle.getString(Constants.BundleKey.USER_ID, "")
            url = bundle.getString(Constants.BundleKey.URL, "")
            postId = bundle.getString(Constants.BundleKey.POST_ID, "")
            messageResponse = bundle.getString(Constants.BundleKey.MESSAGE_RESPONSE, "")
            mediaList = bundle.getString(Constants.BundleKey.MEDIA_LIST, "")
        }
        Log.d("TAG", "onCreate: $url")
        setContent {
            Surface(
                modifier = Modifier.statusBarsPadding()
            ) {
                MedrevPatientTheme {
                    ContainerScreen(
                        startDestination = isComeFor,
                        postId = postId,
                        messageResponse = messageResponse,
                        userId = userId,
                        url = url

                    )
                }
            }
        }
    }
}