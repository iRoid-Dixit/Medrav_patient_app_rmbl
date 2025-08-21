package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.utils.ext.makeUpperCase


/**
 * @param spaceBy use when you are wrapping static/non-list  content
 * */
@Composable
fun HeaderContentWrapper(
    title: String,
    spaceBy: Dp = 0.dp,
    wrapperPadding: Dp = 0.dp,
    makeUpperCase: Boolean = true,
    trailingText: String? = null,
    onAllClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {

    VStack(spaceBy = spaceBy, modifier = modifier) {

        HeaderContentWrapperRow(
            title = title.makeUpperCase(makeUpperCase),
            trailingText = trailingText,
            onAllClick = onAllClick,
            modifier = Modifier
                .padding(horizontal = wrapperPadding)
                .fillMaxWidth(),
        )
        content()
    }
}


@Composable
fun HeaderContentWrapperRow(
    title: String,
    trailingText: String? = null,
    onAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val headerStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontFamily = outFit,
        fontSize = 14.sp,
        color = black25,
        letterSpacing = 2.sp
    )

    HStack(spaceBy = 8.dp, modifier = modifier.fillMaxWidth()) {
        Text(
            text = title, style = headerStyle
        )

        Spacer(modifier = Modifier.weight(1f))

        trailingText?.let {

            HStack (
                4.dp,
                modifier = Modifier.clickable(onClick = onAllClick)
            ) {
                Text(
                    text = trailingText, style = headerStyle.copy(letterSpacing = 0.sp)
                )

                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = "see more",
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 4.dp)
                )
            }
        }

    }
}