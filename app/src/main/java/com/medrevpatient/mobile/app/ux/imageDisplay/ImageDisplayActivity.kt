package com.medrevpatient.mobile.app.ux.imageDisplay
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val bundle = intent.getBundleExtra(Constants.IS_FORM)
        var mediaList = ""
        if (bundle != null) {
            mediaList = bundle.getString(Constants.BundleKey.MEDIA_LIST, "")
        }
        Log.d("TAG", "mediaList: $mediaList")
        setContent {
            MedrevPatientTheme {
                Surface(modifier = Modifier.navigationBarsPadding()) {
                    ImageDisplayScreen(
                        onBackClick = { finish() },
                        mediaListData = mediaList
                    )
                }
            }
        }
    }
}
