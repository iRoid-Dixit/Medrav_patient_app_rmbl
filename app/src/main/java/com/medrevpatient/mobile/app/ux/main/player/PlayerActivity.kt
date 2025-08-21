package com.medrevpatient.mobile.app.ux.main.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {

    val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.handleIntent(intent)

        setContent {
            MedrevPatientTheme {
                Surface(
                    color = Color.Black
                ) {
                    PlayerScreen(viewModel = viewModel)
                }
            }
        }
    }
}