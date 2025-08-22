package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Blue1C
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.YellowDF


@Composable
fun CommunityGuidelinesText(
    onTermsAndConditionsClick: () -> Unit,
    onCheckChange: (Boolean) -> Unit,
    errorMessage: String? = null,

    ) {
    val linkText = "community guidelines"
    val annotatedString = remember {
        buildAnnotatedString {
            append("I agree that this post follows the ")
            val start = length
            append(linkText)
            addStyle(
                style = SpanStyle(
                    color = Blue1C,
                    fontSize = 10.sp,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W400,
                    textDecoration = TextDecoration.Underline
                ),
                start = start,
                end = start + linkText.length
            )
            addStringAnnotation(
                tag = "URL",
                annotation = "community_guidelines",
                start = start,
                end = start + linkText.length
            )
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isChecked by rememberSaveable { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .background(
                        color = if (isChecked) YellowDF else White,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onCheckChange(!isChecked); isChecked = !isChecked }
                    .border(
                        width = 1.5.dp,
                        color = AppThemeColor,
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center

            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isChecked,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        tint = White
                    )
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
            ClickableText(
                text = annotatedString,
                style = TextStyle(
                    color = White,
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W400,
                ),
                onClick = { offset ->
                    annotatedString.getStringAnnotations("URL", offset, offset).firstOrNull()
                        ?.let { annotation ->
                            if (annotation.item == "community_guidelines") {
                                onTermsAndConditionsClick()
                            }
                        }
                }

            )
        }

        if (errorMessage?.isNotEmpty() == true) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = WorkSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 15.dp, top = 10.dp)
            )
        }
    }
}

@Preview
@Composable
fun CommunityGuidelinesTextPreview() {
    CommunityGuidelinesText(
        onTermsAndConditionsClick = {},
        onCheckChange = {}
    )
}


