package com.medrevpatient.mobile.app.utils.customLoader

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.black20

@Preview
@Composable
fun CustomLoader(
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .background(black20, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            CircularProgressIndicator(
                trackColor = androidx.compose.ui.graphics.Color.Transparent,
                color = AppThemeBlue,
                modifier = Modifier.size(65.dp),
                strokeWidth = 3.dp
            )
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "loading",
                modifier = Modifier
                    .size(55.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(White)
            )
        }
    }
}