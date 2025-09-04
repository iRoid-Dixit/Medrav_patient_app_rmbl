package com.medrevpatient.mobile.app.ui.compose.common.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@Preview(showBackground = true)
@Composable
private fun CameraGalleryDialogPreview() {
    CameraGalleryDialog(
        onDismissRequest = {},
        onCameraClick = {},
        onGalleryClick = {},
        galleryText = "Edit Event",
        cameraText = "Delete Event",
        videoText = "video take"
    )
}
@Composable
fun CameraGalleryDialog(
    onDismissRequest: () -> Unit = {},
    galleryText: String,
    cameraText: String,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    videoRecordClick: () -> Unit = {},
    videoText: String? = null,
    videoTextShow: Boolean = false,
    topIcon: Int? = null,
    bottomIcon: Int? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .heightIn(110.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActionButton(
                    text = galleryText,
                    icon = topIcon,
                    onClick = onGalleryClick
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                ActionButton(
                    text = cameraText,
                    icon = bottomIcon,
                    onClick = onCameraClick
                )
                if (videoTextShow) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    ActionButton(
                        text = videoText ?: "",
                        icon = bottomIcon,
                        onClick = videoRecordClick
                    )

                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: Int? = null,
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = AppThemeColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = "User",
                    modifier = Modifier.size(20.dp),

                    )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontFamily = nunito_sans_600,
                    color = Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


