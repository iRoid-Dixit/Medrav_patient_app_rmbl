package com.griotlegacy.mobile.app.ux.container.faq
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.TopBarComponent
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.MineShaft
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.White80
import com.griotlegacy.mobile.app.ui.theme.WorkSans


@ExperimentalMaterial3Api
@Composable
fun FaqScreenScreen(
    navController: NavController,
    viewModel: FaqViewModel = hiltViewModel(),
    screenName: String
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val changePasswordUiState by uiState.contactUsDataFlow.collectAsStateWithLifecycle()
    uiState.event(ContactUsUiEvent.GetScreenName(screenName))
    uiState.event(ContactUsUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = if (screenName == Constants.AppScreen.FQA_SCREEN) "FAQ" else "Legacy Reflection",
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.event(ContactUsUiEvent.BackClick)
                },

                )
        },
        navBarData = null
    ) {
        FaqScreenScreenContent(uiState, screenName = screenName)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun FaqScreenScreenContent(
    uiState: ContactUsUiState,
    screenName: String,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable { keyboardController?.hide() }
            .background(AppThemeColor)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.welcome_to_griot_legacy),
            fontSize = 22.sp,
            color = White,

            fontFamily = WorkSans,
            fontWeight = FontWeight.W600
        )

        Spacer(modifier = Modifier.height(20.dp))
        FaqPagingDataLoad(uiState, screenName = screenName)
    }
}

@Composable
fun FaqPagingDataLoad(uiState: ContactUsUiState, screenName: String) {
    val faqQuestionList = uiState.faqQuestionListFlow.collectAsLazyPagingItems()
    var expandedIndex by remember { mutableIntStateOf(-1) }
    faqQuestionList.loadState.refresh.apply {
        when (this) {
            is LoadState.Error -> {
                TapHereRefreshContent(onClick = { faqQuestionList.retry() })
            }

            is LoadState.Loading -> {
                CustomLoader()
            }

            is LoadState.NotLoading -> {
                if (faqQuestionList.itemCount == 0) {
                    NoDataFoundContent(text = stringResource(R.string.no_faqs_available_at_the_moment))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(faqQuestionList.itemCount) { index ->
                            faqQuestionList[index]?.let { item ->
                                FAQItem(question = item.question ?: "",
                                    answer = item.answer ?: "",
                                    isExpanded = expandedIndex == index,
                                    onToggle = {
                                        expandedIndex = if (expandedIndex == index) -1 else index
                                    },
                                    title = item.title ?: "",
                                    screenName = screenName
                                )
                            }
                        }
                        when (faqQuestionList.loadState.append) {
                            is LoadState.Error -> {
                                item {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.something_went_wrong),
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                color = AppThemeColor,
                                                fontFamily = WorkSans,
                                            )
                                            Text(
                                                modifier = Modifier.clickable {
                                                    faqQuestionList.retry()
                                                },
                                                text = stringResource(id = R.string.tap_here_to_refresh_it),
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                color = AppThemeColor,
                                                fontFamily = WorkSans,
                                            )
                                        }
                                    }
                                }
                            }

                            LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        CircularProgressIndicator(color = White)
                                    }
                                }
                            }

                            is LoadState.NotLoading -> Unit
                        }

                    }

                }
            }
        }
    }

}

@Composable
fun FAQItem(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    title: String,
    screenName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .background(Black)
            .padding(vertical = 8.dp) // Reduced vertical padding
    ) {
        if (screenName == Constants.AppScreen.LEGACY_REFLECTION_SCREEN) {
            Text(
                text = title,
                color = White,
                fontWeight = FontWeight.W800,
                fontFamily = WorkSans,
                lineHeight = 18.sp,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(start = 5.dp, top = 4.dp, bottom = 5.dp) // Reduce top & bottom padding
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = question,
                color = White,
                fontWeight = FontWeight.W500,
                fontFamily = WorkSans,
                lineHeight = 18.sp,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Adjust width to ensure proper alignment
                    .padding(start = 10.dp, top = 4.dp, bottom = 10.dp) // Reduce top & bottom padding
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = if (isExpanded) R.drawable.ic_app_icon else R.drawable.ic_app_icon),

                contentDescription = null,
                colorFilter = ColorFilter.tint(color = White),
                modifier = Modifier
                    .size(25.dp) // Slightly smaller icon
                    .padding(end = 5.dp) // Reduce right padding
            )
        }
        if (isExpanded) {
            Text(
                text = answer,
                color = White80,
                fontFamily = WorkSans,
                lineHeight = 18.sp,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 10.dp, top = 3.dp, bottom = 10.dp)
            )
        }
        HorizontalDivider(thickness = 1.dp, color = MineShaft)
    }
}
@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = ContactUsUiState()
    FaqScreenScreenContent(uiState = uiState, screenName = "")

}






