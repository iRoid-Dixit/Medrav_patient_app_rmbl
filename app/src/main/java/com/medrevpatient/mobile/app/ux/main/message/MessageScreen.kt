package com.medrevpatient.mobile.app.ux.main.message

import android.R.attr.text
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.UserData
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.Alto
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.BlueChalk
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.Gray60
import com.medrevpatient.mobile.app.ui.theme.GrayBD
import com.medrevpatient.mobile.app.ui.theme.Green4C
import com.medrevpatient.mobile.app.ui.theme.Martinique
import com.medrevpatient.mobile.app.ui.theme.Martinique50
import com.medrevpatient.mobile.app.ui.theme.Mercury
import com.medrevpatient.mobile.app.ui.theme.Silver
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.ui.theme.violetsAreBlue
import com.medrevpatient.mobile.app.ui.theme.whiteBrush
import kotlin.text.Typography.bullet

@ExperimentalMaterial3Api
@Composable
fun MessageScreen(
    navController: NavController,
    viewModel: MessageViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val settingUiState by uiState.messageUiDataFlow.collectAsStateWithLifecycle()

    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                isBackVisible = true,
                titleText = "Support & Messages"
            )
        },
        navBarData = null
    ) {
        MessageScreenContent(uiState, uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun MessageScreenContent(
    uiState: MessageUiState,
    event: (MessageUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        HeaderComponent()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppButtonComponent(
                text = "Doctor’s Team", onClick = {},
                modifier = Modifier.weight(1f)
            )
            AppButtonComponent(
                drawableResId = R.drawable.ic_call, text = "Call", onClick = {},
                modifier = Modifier.weight(1f),
                backgroundBrush = whiteBrush,
                borderColors = AppThemeColor,
                textColor = AppThemeColor
            )

        }

        Text(
            text = "Recent Conversations",
            style = TextStyle(
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = nunito_sans_600
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        SupportTeamComponent { }

        QuestionsComponent()
    }
}

@Preview
@Composable
private fun HeaderComponent(
    title: String = "Our team replies within 1 hour",
    description: String = "Support hours: 8 AM - 5 PM EST",
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(15.dp),
        color = BlueChalk,
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_with_bg_clock),
                contentDescription = "Message",
                modifier = Modifier
                    .size(32.dp)
            )

            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        color = SteelGray,
                        fontSize = 14.sp,
                        fontFamily = nunito_sans_600
                    )
                )

                Text(
                    text = description,
                    style = TextStyle(
                        color = Martinique,
                        fontSize = 12.sp,
                        fontFamily = nunito_sans_400
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun SupportTeamComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { /* No - Op*/ }
) {
    Surface(
        shape = RoundedCornerShape(15.dp),
        color = White,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 5.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bg_calling),
                contentDescription = "Calling",
                modifier = Modifier
                    .size(40.dp)
            )


            Column {

                Row(modifier = Modifier.fillMaxWidth()) {

                    Text(
                        text = "Support Team",
                        style = TextStyle(
                            color = Black,
                            fontSize = 16.sp,
                            fontFamily = nunito_sans_600
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = buildAnnotatedString {
                            append("2 min ago")
                            withStyle(SpanStyle(color = violetsAreBlue, fontSize = 24.sp)) {
                                append(" $bullet")
                            }
                        },
                        style = TextStyle(
                            color = GrayBD,
                            fontSize = 14.sp,
                            fontFamily = nunito_sans_400
                        )
                    )
                }

                Text(
                    text = "Hi! I'm here to help with your medication question. Let me check with our pharmacy team...",
                    style = TextStyle(
                        color = Martinique50,
                        fontSize = 14.sp,
                        fontFamily = nunito_sans_400
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onClick,
                        colors = ButtonDefaults.textButtonColors(containerColor = Green4C.copy(.1f))
                    ) {
                        Text("Active", color = Green4C, fontFamily = nunito_sans_600)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun QuestionsComponent(
    modifier: Modifier = Modifier,
) {

    Surface(
        shape = RoundedCornerShape(15.dp),
        color = White,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 5.dp
    ) {

        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Frequently Asked Questions",
                style = TextStyle(
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = nunito_sans_600
                )
            )

            UserData.frequentlyAskedQuestions.forEach { question ->
                QuestionItemComponent(questionItem = question)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun QuestionItemComponent(
    modifier: Modifier = Modifier,
    questionItem: UserData.QuestionItem = UserData.frequentlyAskedQuestions[0],
) {

    var isExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(White, Alto)
                ),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
            .animateContentSize()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                questionItem.question,
                modifier = Modifier.weight(1f),
                color = SteelGray,
                fontSize = 14.sp,
                fontFamily = nunito_sans_400
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = Silver,
                modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
            )
        }

        if (isExpanded) {
            Text(
                questionItem.answer, color = Martinique50,
                fontSize = 12.sp,
                fontFamily = nunito_sans_400
            )
        }

    }
}

@Preview
@Composable
private fun Preview() {

    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                isBackVisible = true,
                titleText = "Support & Messages"
            )
        },
        navBarData = null
    ) {
        MessageScreenContent(
            uiState = MessageUiState(),
            event = {},
        )
    }
}