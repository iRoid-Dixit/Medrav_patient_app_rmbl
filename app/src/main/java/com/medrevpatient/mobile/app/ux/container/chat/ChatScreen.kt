package com.medrevpatient.mobile.app.ux.container.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.model.domain.response.chat.ChatResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.ChatTopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.CustomDropdownMenu
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.noFoundRelatedComponent.NoDataFoundContent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.GrayC0
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalMaterial3Api
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel(),
    messageResponse: String
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val menuItems =
        listOf(
            "Group Info" to {
                uiState.event(ChatUiEvent.GroupDetailsUpdate)

            },
            "Add Members" to {
                uiState.event(ChatUiEvent.AddNewMember)
            }
        )
    val chatUiState by uiState.chatDataFlow.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        uiState.event(ChatUiEvent.GetContext(context))
        uiState.event(ChatUiEvent.MessageResponse(messageResponse))
    }
    BackHandler(onBack = {
        uiState.event(ChatUiEvent.BackClick)
    })
    var expanded by remember { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            ChatTopBarComponent(
                isBackVisible = true,
                onClick = {
                    uiState.event(ChatUiEvent.BackClick)
                },
                isTrailingIconVisible = if (chatUiState?.messageResponse?.type == 1) false else if (chatUiState?.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) false else true,
                onTrailingIconClick = {
                    if (chatUiState?.messageResponse?.type == 2) {
                        expanded = true
                    }
                },
                trailingIcon = R.drawable.ic_app_icon,
                userProfile = if (chatUiState?.messageResponse?.type == 1) chatUiState?.messageResponse?.receiverProfile
                    ?: "" else if (chatUiState?.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) chatUiState?.chatData?.profileImage
                    ?: "" else chatUiState?.messageResponse?.groupImage ?: "",
                userName = if (chatUiState?.messageResponse?.type == 1) chatUiState?.messageResponse?.receiverName
                    ?: "" else if (chatUiState?.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) chatUiState?.chatData?.name
                    ?: "" else chatUiState?.messageResponse?.groupName ?: ""
            )
            CustomDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                menuItems = menuItems,
                offsetY = 30.dp
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
            }
        )
    ) {
        ChatScreenContent(uiState, chatUiState)
    }
    if (chatUiState?.showLoader == true) {
        CustomLoader()
    }
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.event(ChatUiEvent.InitSocketListener(context))
            }
            else -> {}
        }

    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ChatScreenContent(uiState: ChatUiState, chatUiState: ChatDataState?) {
    val chatDataList by uiState.chatDataList.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .background(AppThemeColor)
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {

        Box(modifier = Modifier.weight(1f)) {
            MessageListContent(chatUiState, chatList = chatDataList, uiState)
        }
        ChatMsgTextField(
            uiState,
            chatUiState
        )

    }
}
@Composable
fun MessageListContent(
    chatUiState: ChatDataState?,
    chatList: List<ChatResponse>,
    uiState: ChatUiState,
) {
    val context = LocalContext.current
    val lazyColumnListState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyColumnListState.layoutInfo
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 1 && totalItemsCount > 0
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && chatUiState?.isLoading == false) {
            uiState.event(ChatUiEvent.OnLoadNextPage)
        }
    }
    val groupedRemoteMsg =
        chatList.groupBy { item ->
            item.let {
                com.medrevpatient.mobile.app.utils.DateTimeUtils.getMessageTimestamp(
                    it.createdAt ?: 0,
                    context
                )
            }
        }
    val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    Column {
        if (chatList.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                state = lazyColumnListState,
                reverseLayout = true
            ) {
                groupedRemoteMsg.forEach { (dateStampStatic, messageStatic) ->
                    items(messageStatic) { item ->
                        val isFromMe = item.senderId == chatUiState?.userId
                        if (isFromMe) {
                            SenderChatItem(item)
                        } else {
                            ReceiverChatItem(item)  // Fixed the typo here
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (todayDate == dateStampStatic) stringResource(R.string.today) else dateStampStatic,
                                fontFamily = WorkSans,
                                fontWeight = W400,
                                color = White,
                                fontSize = 12.sp
                            )
                        }

                    }
                }
                if (chatUiState?.isLoading == true) {
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
            }
        } else {
            if (chatUiState?.isApiStatus == true) {
                NoDataFoundContent(
                    if (chatUiState.messageResponse?.type == 1) stringResource(R.string.no_messages_available) else if (chatUiState.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) stringResource(
                        R.string.no_messages_available
                    ) else stringResource(R.string.no_messages_in_this_group_yet)
                )
            }
        }
        LaunchedEffect(chatList.size) {
            lazyColumnListState.animateScrollToItem(0)
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatMsgTextField(uiState: ChatUiState, chatUiState: ChatDataState?) {
    var textLines by remember { mutableIntStateOf(1) }
    Row(
        modifier = Modifier.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .background(AppThemeColor)

        ) {
            Surface(
                contentColor = AppThemeColor,
                shape = RoundedCornerShape(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(color = AppThemeColor)
                        .heightIn(max = (4 * 24).dp)
                        .fillMaxWidth()
                ) {
                    BasicTextField(
                        value = chatUiState?.sendText ?: "",
                        onValueChange = {
                            uiState.event(ChatUiEvent.SendMessage(it))
                            textLines = it.count { ch -> ch == '\n' } + 1
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White50),
                        visualTransformation = VisualTransformation.None,
                        readOnly = false,
                        enabled = true,
                        cursorBrush = SolidColor(White50)
                    ) {
                        TextFieldDefaults.DecorationBox(
                            value = chatUiState?.sendText ?: "",
                            innerTextField = it,
                            singleLine = false,
                            enabled = true,

                            visualTransformation = VisualTransformation.None,
                            placeholder = {
                                if (chatUiState?.sendText?.isEmpty() == true) {
                                    Text(
                                        text = stringResource(id = R.string.write_here),
                                        fontFamily = WorkSans,
                                        fontSize = 14.sp,
                                        fontWeight = W500,
                                        color = White50,
                                    )
                                }
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                                top = 13.dp, bottom = 13.dp, end = 5.dp
                            ),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Gray2F,
                                focusedIndicatorColor = Gray2F,
                                focusedTextColor = White50,
                                unfocusedTextColor = White50,
                                focusedContainerColor = Gray2F,
                                unfocusedContainerColor = Gray2F,
                                errorContainerColor = Gray2F,
                                errorIndicatorColor = Gray2F,

                                ),
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(start = 15.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon),
            contentDescription = null,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    uiState.event(ChatUiEvent.OnMessageSendButtonClick)
                }

        )
    }
}


@Composable
fun SenderChatItem(item: ChatResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        // Message content
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = White,
                        shape = RoundedCornerShape(
                            topStart = 5.dp,
                            topEnd = 5.dp,
                            bottomStart = 5.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = item.message ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    fontFamily = WorkSans,
                    fontWeight = W400,
                    color = Color.Black
                )
            }

            // Timestamp below message
            Text(
                text = AppUtils.formatTimestamp(item.createdAt),
                fontFamily = WorkSans,
                fontWeight = W400,
                color = White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Profile image on right
        Column {
            AsyncImage(
                model = item.profileImage,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_app_icon),
                placeholder = painterResource(id = R.drawable.ic_app_icon),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ReceiverChatItem(item: ChatResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        // Profile image on left
        Column {
            AsyncImage(
                model = item.profileImage,
                contentDescription = "Profile Picture",
                error = painterResource(id = R.drawable.ic_app_icon),
                placeholder = painterResource(id = R.drawable.ic_app_icon),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))

        // Message content
        Column {
            Box(
                modifier = Modifier
                    .background(
                        color = GrayC0,
                        shape = RoundedCornerShape(
                            topStart = 5.dp,
                            topEnd = 5.dp,
                            bottomEnd = 5.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = item.message ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    fontFamily = WorkSans,
                    fontWeight = W400,
                    color = Color.Black
                )
            }
            // Timestamp below message
            Text(
                text = AppUtils.formatTimestamp(item.createdAt),
                fontFamily = WorkSans,
                fontWeight = W400,
                color = White,
                fontSize = 12.sp,
            )
        }
    }
}

@Preview
@Composable
fun TribeListContentPreview() {
    val item = ChatResponse()
    SenderChatItem(item)

}






