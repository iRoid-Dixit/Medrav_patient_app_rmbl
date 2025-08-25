package com.medrevpatient.mobile.app.ui.compose.common
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray5A
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.YellowDF
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_300
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@Composable
fun LogInSignInNavText(
    message: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ) {
    Column {
        Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
            Text(
                text = message,
                fontFamily = nunito_sans_400,
                color = Gray5A,

                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = actionText,
                fontFamily = nunito_sans_600,
                fontSize = 14.sp,
                color = AppThemeColor,
                modifier = modifier.clickable(onClick = onClick)

            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}


