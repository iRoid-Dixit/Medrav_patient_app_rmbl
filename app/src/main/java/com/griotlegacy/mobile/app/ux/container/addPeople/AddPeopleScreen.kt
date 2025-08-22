package com.griotlegacy.mobile.app.ux.container.addPeople

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppNavBarData
import com.griotlegacy.mobile.app.navigation.scaffold.AppNavBarType
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.AppButtonComponent
import com.griotlegacy.mobile.app.ui.compose.common.AppInputTextField
import com.griotlegacy.mobile.app.ui.compose.common.TopBarComponent
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.griotlegacy.mobile.app.ui.compose.common.noFoundRelatedComponent.TapHereRefreshContent
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.WorkSans

@ExperimentalMaterial3Api
@Composable
fun AddPeopleScreen(
    navController: NavController,
    viewModel: AddPeopleViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val allPeopleUiState by uiState.allPeopleDataFlow.collectAsStateWithLifecycle()
    uiState.event(AddPeopleUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = if (allPeopleUiState?.screen == Constants.AppScreen.GROUP_MEMBER_SCREEN) "Add Member" else stringResource(
                    R.string.add_people
                ),
                isBackVisible = true,
                onClick = {
                    uiState.event(AddPeopleUiEvent.BackClick)
                },
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {

                AppButtonComponent(
                    onClick = {
                        uiState.event(AddPeopleUiEvent.OnDoneButtonClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    text = stringResource(R.string.done)
                )
            }
        )
    ) {
        AddPeopleScreenContent(uiState, uiState.event, allPeopleUiState)
    }
    if (allPeopleUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun AddPeopleScreenContent(
    uiState: AddPeopleUiState,
    event: (AddPeopleUiEvent) -> Unit,
    allPeopleUiState: AddPeopleDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
    ) {
        if (allPeopleUiState?.screen == Constants.AppScreen.GROUP_MEMBER_SCREEN) {
            Spacer(modifier = Modifier.size(15.dp))
            AppInputTextField(
                value = allPeopleUiState.searchMember,
                onValueChange = { event(AddPeopleUiEvent.SearchMember(it)) },
                errorMessage = "",
                isLeadingIconVisible = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                header = "Search member",
                leadingIcon = R.drawable.ic_app_icon,
            )

        }
        val tribeMemberList = uiState.allPeopleListFlow.collectAsLazyPagingItems()
        val lazyColumnListState = rememberLazyListState()
        tribeMemberList.loadState.refresh.apply {
            when (this) {
                is LoadState.Error -> {
                    TapHereRefreshContent()
                }

                is LoadState.Loading -> {
                    CustomLoader()
                }

                is LoadState.NotLoading -> {
                    if (tribeMemberList.itemCount == 0) {
                        if (allPeopleUiState?.screen == Constants.AppScreen.GROUP_MEMBER_SCREEN) {
                            if (allPeopleUiState.searchMember.isNotBlank()) {
                                NoDataFoundContent("No search results found")
                            } else {
                                NoDataFoundContent("no member found")
                            }
                        } else {
                            NoDataFoundContent("No member found")
                        }
                    } else {
                        LazyColumn(
                            state = lazyColumnListState,
                            modifier = Modifier.padding(vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)

                        )
                        {
                            items(tribeMemberList.itemCount) { index ->
                                tribeMemberList[index]?.let { item ->
                                    ItemMemberPeople(
                                        item,
                                        uiState = uiState,
                                        onItemSelectionChanged = { selectedMember, isSelected ->
                                            if (isSelected) {
                                                uiState.event(
                                                    AddPeopleUiEvent.SelectedMember(
                                                        selectedMember.id ?: ""
                                                    )
                                                )
                                            } else {
                                                //    uiState.selectedMembers - selectedMember
                                                uiState.event(
                                                    AddPeopleUiEvent.SelectedMember(
                                                        selectedMember.id ?: ""
                                                    )
                                                )
                                            }
                                        })

                                }
                            }
                            when (tribeMemberList.loadState.append) {
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
                                                    fontSize = 15.sp,
                                                    maxLines = 1,
                                                    color = Black,
                                                    fontFamily = WorkSans
                                                )

                                                Text(
                                                    modifier = Modifier.clickable { tribeMemberList.retry() },
                                                    text = stringResource(id = R.string.tap_here_to_refresh_it),
                                                    fontSize = 15.sp,
                                                    maxLines = 1,
                                                    color = Black,
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
}

@Composable
fun ItemMemberPeople(
    member: SearchPeopleResponse,
    uiState: AddPeopleUiState,
    onItemSelectionChanged: (SearchPeopleResponse, Boolean) -> Unit // Callback to update list
) {
    val allPeopleUiState by uiState.allPeopleDataFlow.collectAsStateWithLifecycle()

    var checkedState by rememberSaveable {
        mutableStateOf(allPeopleUiState?.selectedMembers?.contains(member.id) == true)
    }

    val checkedStateItem = if (checkedState)
        Pair(R.drawable.ic_app_icon, "Checked")
    else
        Pair(R.drawable.ic_app_icon, "Unchecked")

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = member.profileImage,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.ic_app_icon),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_app_icon),
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
                .weight(1f),
        ) {
            Text(
                text = member.name ?: "",
                modifier = Modifier.padding(start = 10.dp),
                color = White,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    checkedState = !checkedState
                    onItemSelectionChanged(member, checkedState) // Notify parent
                }
        ) {
            IconButton(onClick = {
                checkedState = !checkedState
                onItemSelectionChanged(member, checkedState) // Notify parent
            }) {
                Image(
                    painter = painterResource(id = checkedStateItem.first),
                    contentDescription = checkedStateItem.second
                )
            }
        }
    }
}

@Preview
@Composable
fun TribeListContentPreview() {
    val uiState = AddPeopleUiState()
    val allPeopleUiState by uiState.allPeopleDataFlow.collectAsStateWithLifecycle()
    AddPeopleScreenContent(
        uiState = uiState,
        event = uiState.event,
        allPeopleUiState = allPeopleUiState
    )

}






