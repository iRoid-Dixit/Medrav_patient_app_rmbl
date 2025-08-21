package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@Composable
fun IconTitleArrowItem(
    backgroundColor: Color = MineShaft.copy(alpha = 0.02f),
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).noRippleClickable { onClick() },
        shape = RoundedCornerShape(25),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = null
            )
            Text(
                text = text,
                color = MineShaft,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontFamily = outFit,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_ahead),
                contentDescription = null
            )
        }

    }
}

@Preview
@Composable
fun Preview(modifier: Modifier = Modifier) {
    IconTitleArrowItem(
        text = "Profile",
        icon = R.drawable.ic_email,
        backgroundColor = white

    ) { }
}