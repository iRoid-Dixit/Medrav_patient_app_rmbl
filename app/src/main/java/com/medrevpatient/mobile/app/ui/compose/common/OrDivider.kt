package com.medrevpatient.mobile.app.ui.compose.common
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.ShuttleGray
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_300
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.R


@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_line_left),
            modifier = Modifier
                .weight(1f),
            contentDescription = null
        )
        Text(
            text = "or continue with",
            modifier = Modifier.padding(horizontal = 8.dp),
            color = ShuttleGray,
            fontSize = 14.sp,
            fontFamily = nunito_sans_400,
        )
        Image(
            painter = painterResource(id = R.drawable.ic_line_left),
            modifier = Modifier
                .weight(1f),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrDividerPreview() {
    OrDivider()
}



