package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton


@Preview
@Composable
private fun SuccessFullSheetContentPreview() {
    SuccessSheetContent(
        icon = drawable.goal,
        title = "Goal created successfully.",
        description = "You can see your created Goal in My Goals screen.",
        btnText = "See My Goals",
        onClick = {}
    )
}

@Composable
fun SuccessSheetContent(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    btnText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    VStack(8.dp, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            modifier = Modifier.size(74.dp)
        )

        Spacer(Modifier.padding(8.dp))

        Text(
            text = title,
            fontWeight = W800,
            fontFamily = outFit,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Text(
            text = description,
            fontWeight = W300,
            fontFamily = outFit,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(Modifier.padding(8.dp))

        SkaiButton(
            text = btnText,
            onClick = onClick
        )

        Spacer(Modifier.padding(4.dp))
    }

}
