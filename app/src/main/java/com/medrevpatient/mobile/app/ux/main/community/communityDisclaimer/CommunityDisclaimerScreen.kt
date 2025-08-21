package com.medrevpatient.mobile.app.ux.main.community.communityDisclaimer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton

@Composable
fun CommunityDisclaimerScreen(
    modifier: Modifier = Modifier,
    viewModel: CommunityDisclaimerViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {},
        containerColor = white,
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->
        if (uiState.isLoading) DialogLoader()
        CommunityDisclaimerContent(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()), uiState, viewModel::event, viewModel
        )
    }
}

@Composable
fun CommunityDisclaimerContent(modifier: Modifier, uiState: CommunityDisclaimerUiState, event: (CommunityDisclaimerUiEvent) -> Unit, viewModel: CommunityDisclaimerViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(white)
            .padding(18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable { viewModel.popBackStack() }
        )

        Image(
            painter = painterResource(id = R.drawable.unselected_community),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterHorizontally)
                .offset(y = (-15).dp)
        )

        TitleDualFont(
            modifier = Modifier.fillMaxWidth(),
            color = MineShaft,
            fontWeightBold = FontWeight.Bold,
            fontWeightRegular = FontWeight.Light,
            fontSize = 20,
            fontFamilyBold = outFit,
            fontFamilyRegular = outFit,
            titlePart1 = stringResource(id = R.string.skai),
            titlePart2 = stringResource(id = R.string.fitness)
        )
        Text(
            text = stringResource(R.string.community).uppercase(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Light,
            color = MineShaft,
            fontFamily = outFit,
            textAlign = TextAlign.Center,
            style = TextStyle(
                letterSpacing = 0.7.em
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 3.dp)
        )
        Text(
            text = stringResource(R.string.community_txt),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = MineShaft,
            fontFamily = outFit,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 3.dp)
        )

        Text(
            text = stringResource(R.string.community_disclaimer),
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = MineShaft,
            fontFamily = outFit,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )

        val list = stringArrayResource(id = R.array.community_array)
        list.forEach {
            CommunityTextListItem(it)
        }
        Row(
            modifier = Modifier.padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCheckBox(event)
            Spacer(modifier = Modifier.padding(6.dp))
            Text(
                text = stringResource(R.string.please_check_guidelines),
                style = TextStyle(
                    color = MineShaft,
                    fontSize = 14.sp,
                    lineHeight = 15.sp,
                    fontFamily = outFit,
                    fontWeight = FontWeight.Medium,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
        SkaiButton(
            text = stringResource(R.string.agree_to_guidelines),
            makeUpperCase = true,
            innerPadding = PaddingValues(horizontal = 18.dp, vertical = 19.dp),
            onClick = { event(CommunityDisclaimerUiEvent.PerformAgreeToGuidelinesClick) },
            color = MineShaft,
            textStyle = TextStyle(
                color = white,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = outFit,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            ),
            elevation = 0.dp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(top = 30.dp)
        )

    }
}

@Composable
fun CommunityTextListItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "\u2022",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MineShaft
            ),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = MineShaft,
                fontFamily = outFit,
                textAlign = TextAlign.Justify
            )
        )
    }
}

@Composable
private fun CustomCheckBox(event: (CommunityDisclaimerUiEvent) -> Unit) {
    var checked by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        if (checked) MineShaft else Color.Transparent,
        label = "color"
    )

    Box(modifier = Modifier
        .border(1.dp, MineShaft, RoundedCornerShape(6.dp))
        .size(20.dp)
        .drawBehind {
            val cornerRadius =
                6.dp.toPx()
            drawRoundRect(
                color = animatedColor,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
        .clip(RoundedCornerShape(6.dp))
        .clickable {
            checked = !checked
            event(CommunityDisclaimerUiEvent.ReadGuidelinesCheckmark(if (checked) 1 else 0))
        }
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            AnimatedVisibility(
                checked,
                enter = scaleIn(initialScale = 0.5f),
                exit = shrinkOut(shrinkTowards = Alignment.Center)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = "checked",
                    tint = White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Surface {
        CommunityDisclaimerContent(modifier = Modifier.fillMaxSize(), CommunityDisclaimerUiState(), event = {}, viewModel = hiltViewModel())
    }
}