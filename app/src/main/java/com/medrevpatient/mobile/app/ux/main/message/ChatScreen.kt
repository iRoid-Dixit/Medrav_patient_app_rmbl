package com.medrevpatient.mobile.app.ux.main.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.UserData
import com.medrevpatient.mobile.app.data.source.local.UserData.sampleChatList
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.GrayBD
import com.medrevpatient.mobile.app.ui.theme.Magnolia
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.Transparent
import com.medrevpatient.mobile.app.ui.theme.White

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {

    AppScaffold(
        modifier = modifier,
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                isBackVisible = true,
                titleText = "Doctor’s Team",
                titleIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_person_doctor),
                        contentDescription = null
                    )
                },
                subTitleText = { Text("Online now", color = White, fontSize = 14.sp) }
            )
        },
        navBarData = AppNavBarData(AppNavBarType.NAV_BAR, navBar = { BottomInputComponent { } })
    ) { ChatContent(modifier = modifier.fillMaxSize()) }
}

@Composable
fun ChatContent(modifier: Modifier = Modifier) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items = sampleChatList) { item ->
            ChatItemComponent(
                modifier = modifier
                    .fillMaxWidth(),
                chat = item
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatItemComponent(
    modifier: Modifier = Modifier,
    chat: UserData.Chats = sampleChatList[2],
) {
    val alignment = if (chat.message.isMe) Alignment.End else Alignment.Start

    val shape = if (chat.message.isMe)
        RoundedCornerShape(
            topStart = 15.dp,
            topEnd = 0.dp,
            bottomStart = 15.dp,
            bottomEnd = 15.dp,
        )
    else RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 18.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp
    )

    //For Arrangement
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = alignment
    ) {

        //For Item
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!chat.message.isMe) {
                Image( //TODO: Replace with user image url using Coil or Glide
                    painter = painterResource(R.drawable.ic_bg_calling),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = if (chat.message.isMe) Alignment.End else Alignment.Start,
            ) {

                Surface(
                    color = Magnolia,
                    modifier = Modifier.padding(
                        start = if (chat.message.isMe) 25.dp else 0.dp,
                        end = if (chat.message.isMe) 0.dp else 25.dp,
                    ),
                    shape = shape
                ) {
                    Text(
                        text = chat.message.content,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Text(
                    text = chat.formatedTime,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black.copy(0.2f)),
                    fontSize = 8.sp,
                )
            }

            if (chat.message.isMe) {
                //TODO: Replace with user image url using Coil or Glide
                Image(
                    painter = painterResource(R.drawable.ic_bg_calling),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }

}


@Preview(showBackground = true)
@Composable
private fun BottomInputComponent(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
) {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        maxLines = 1,
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            textAlign = TextAlign.Start
        ),
        cursorBrush = SolidColor(AppThemeColor),
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(86.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_media_pin),
                        contentDescription = null,
                        tint = GrayBD
                    )
                }

                //TextField
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(15.dp))
                        .border(1.dp, GrayBD, RoundedCornerShape(15.dp))
                        .background(Color.Transparent)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        ), // controls text vertical centering
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Type your message...",
                            color = GrayBD,
                            textAlign = TextAlign.Start
                        )
                    } else {
                        innerTextField()
                    }


                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_emoji),
                        contentDescription = "Select Emoji",
                        tint = GrayBD,
                    )

                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_send),
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppThemeColor,
                                        SteelGray
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }
        },
        modifier = modifier.border(
            brush = Brush.verticalGradient(listOf(GrayBD, White, Transparent)),
            width = 1.dp,
            shape = RoundedCornerShape(45)
        )
    )
}

@Preview
@Composable
private fun ChatScreenPreview() {
    ChatScreen()
}