package com.griotlegacy.mobile.app.ux.main.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.AppInputTextField
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.MineShaft
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.WorkSans
import com.griotlegacy.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    BackHandler(onBack = {
        uiState.event(SearchUiEvent.BackClick)

    })
    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {

            Column(
                modifier = Modifier
                    .background(AppThemeColor)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .requiredHeight(54.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)

                ) {
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            uiState.event(SearchUiEvent.NavigateToNotification)
                        }
                    )
                }
            }

        },
        navBarData = null
    ) {
        SearchScreenContent(uiState = uiState, event = uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun SearchScreenContent(uiState: SearchUiState, event: (SearchUiEvent) -> Unit) {
    val searchUiState by uiState.searchUiDataFlow.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(AppThemeColor)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        AppInputTextField(
            value = searchUiState?.search ?: "",
            onValueChange = { event(SearchUiEvent.SearchValueChange(it)) },
            errorMessage = "",
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            header = "Search Here",
            leadingIcon = R.drawable.ic_app_icon,
        )
        SearchListComponent(uiState)
    }
}

@Composable
fun SearchListComponent(uiState: SearchUiState) {
    val allPeopleLis = uiState.allPeopleListFlow.collectAsLazyPagingItems()
    allPeopleLis.loadState.refresh.apply {
        when (this) {
            is LoadState.Error -> {
                TapHereRefreshContent()
            }

            is LoadState.Loading -> {
                CustomLoader()
            }

            is LoadState.NotLoading -> {
                if (allPeopleLis.itemCount == 0) {
                    if (uiState.searchUiDataFlow.value?.search?.isNotBlank() == true) {
                        NoDataFoundContent("No search results found")
                    } else {
                        NoDataFoundContent("No users found")
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(top = 30.dp))
                    {
                        items(allPeopleLis.itemCount) { index ->
                            allPeopleLis[index]?.let { item ->
                                SearchItem(item, onSearchItemCLick = {
                                    uiState.event(
                                        SearchUiEvent.NavigateToUserProfile(
                                            item.id ?: ""
                                        )
                                    )
                                })
                            }
                            if (index != allPeopleLis.itemCount - 1) {
                                Spacer(modifier = Modifier.height(15.dp))
                                HorizontalDivider(thickness = 1.dp, color = MineShaft)
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                        when (allPeopleLis.loadState.append) {
                            is LoadState.Error -> {
                                item {
                                    TapHereRefreshContent()
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
fun SearchItem(item: SearchPeopleResponse, onSearchItemCLick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onSearchItemCLick()
            }) {
        AsyncImage(
            model = item.profileImage,
            contentDescription = stringResource(R.string.profile),
            modifier = Modifier
                .size(60.dp)
                .clip(shape = CircleShape),
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            error = painterResource(id = R.drawable.ic_app_icon),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.name ?: "",
            fontFamily = WorkSans,
            fontSize = 16.sp,
            color = White,
            fontWeight = FontWeight.W400
        )
    }
}


@Preview
@Composable
private fun Preview() {
    val uiState = SearchUiState()
    Surface {
        SearchScreenContent(uiState = uiState, event = {})
    }
}