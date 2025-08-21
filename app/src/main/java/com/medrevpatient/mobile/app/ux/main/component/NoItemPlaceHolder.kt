package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W200
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25

@Composable
fun NoItemPlaceHolder(
    title: String,
    subTitle: String,
    icon: Int,
    btnText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    VStack(
        0.dp, modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(12))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(aliceBlue.copy(.6f), aliceBlue)
                    )
                )
                .padding(18.dp)
        )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.padding(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = black25,
            fontWeight = SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.padding(4.dp))
        Text(
            text =subTitle,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = W200, color = black25),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.padding(12.dp))
        SkaiButton(text = btnText, onClick = onClick)
    }
}