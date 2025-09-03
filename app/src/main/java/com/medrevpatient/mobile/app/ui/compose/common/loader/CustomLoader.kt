package com.medrevpatient.mobile.app.ui.compose.common.loader

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White30
import com.medrevpatient.mobile.app.ui.theme.WorkSans


/*
@Preview
@Composable
fun CustomLoader(
    @DrawableRes icon: Int? = null
) {
    Dialog(onDismissRequest = { */
/*TODO*//*
 }, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(trackColor = androidx.compose.ui.graphics.Color.Transparent, color = PinkE9, modifier = Modifier.size(60.dp))
            Image(painter = painterResource(id = R.drawable.ic_app_logo), contentDescription = "loading", modifier = Modifier
                .size(45.dp)
                .clip(CircleShape))

        }
    }
}*/
@Preview
@Composable
fun CustomLoader() {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(White, RoundedCornerShape(16.dp)) // clean white card look
                .padding(24.dp)
        ) {
            // Circular progress (foreground)
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                modifier = Modifier.size(60.dp),
                color = AppThemeColor,        // Active ring → Purple
                trackColor = White30          // Track ring → faint white
            )

            // App Logo in center
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "loading",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
        }
    }
}


@Preview
@Composable
fun CustomLoaderWithMessage(message: String = "Compressing video... Please wait") {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.DarkGray, RoundedCornerShape(10.dp))
                .padding(15.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular progress (foreground)
                CircularProgressIndicator(
                    trackColor = Color.Gray,
                    color = Color.White,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


