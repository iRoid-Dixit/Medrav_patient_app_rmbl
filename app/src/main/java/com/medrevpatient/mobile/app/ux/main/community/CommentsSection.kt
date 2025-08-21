package com.medrevpatient.mobile.app.ux.main.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.Comments
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.ColorOsloGray
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black50
import com.medrevpatient.mobile.app.ui.theme.black94
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.main.component.OutlineTextFieldWithTrailing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheet(
    commentList: LazyPagingItems<Comments>,
    commentValue: String,
    onCommentValueChange: (String) -> Unit,
    sendCommentClick: (String) -> Unit,
    postId: String,
    /*uiState: CommunityUiState,*/
    /*event: (CommunityUiEvent) -> Unit,*/
    shouldShowSheet: Boolean = true,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(shouldShowSheet) {
        if (shouldShowSheet) sheetState.show() else sheetState.hide()
    }

    if (shouldShowSheet)
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = white,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            dragHandle = null
        ) {
            VStack(
                spaceBy = 15.dp,
                modifier = Modifier.fillMaxHeight(0.55f)
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_top_handle),
                    contentDescription = "",
                    modifier = Modifier.padding(top = 5.dp)
                )
                Text(
                    text = stringResource(R.string.comments),
                    fontSize = 20.sp,
                    fontFamily = outFit,
                    fontWeight = Bold,
                    lineHeight = 25.sp,
                    color = black25
                )
                if (commentList.itemCount == 0 && commentList.loadState.refresh !is androidx.paging.LoadState.Loading) {
                    Text(
                        text = stringResource(R.string.no_comments),
                        fontSize = 14.sp,
                        fontFamily = outFit,
                        fontWeight = Medium,
                        color = black50,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 150.dp)
                    )
                } else {
                    CommentsContent(
                        commentList = commentList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .padding(top = 5.dp)
                    )
                }

            }

            HStack(
                spaceBy = 10.dp, modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 15.dp, top = 20.dp)
            ) {
                OutlineTextFieldWithTrailing(
                    value = commentValue,//uiState.commentToPost,
                    onValueChange = { onCommentValueChange(it) /*event(CommunityUiEvent.OnCommentPostValueChange(it))*/ },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.make_a_comment),
                            fontFamily = outFit,
                            fontWeight = W300,
                            color = black25,
                            fontSize = 14.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_send_comment),
                    contentDescription = "",
                    modifier = Modifier.noRippleClickable {
                        sendCommentClick(postId)
                        //event(CommunityUiEvent.PerformSendCommentClick(uiState.postId))
                    }
                )
            }

        }
}

@Composable
private fun CommentsContent(
    modifier: Modifier = Modifier,
    commentList: LazyPagingItems<Comments>
) {
    PagingResultHandler(commentList) { pagingState ->
        LazyColumn(
            modifier = modifier
        ) {
            items(pagingState.itemCount) { comment ->
                val comments = commentList[comment] ?: return@items
                CommentItem(
                    modifier = Modifier.fillMaxWidth(),
                    data = comments
                )
            }
        }
    }
}

@Composable
fun CommentItem(modifier: Modifier, data: Comments) {
    HStack(spaceBy = 15.dp, modifier = modifier.padding(bottom = 15.dp), verticalAlignment = Alignment.Top) {
        AsyncImage(
            model = data.user?.profileImage,
            placeholder = painterResource(R.drawable.img_portrait_placeholder),
            error = painterResource(R.drawable.img_portrait_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            clipToBounds = true,
            modifier = Modifier
                .clip(RoundedCornerShape(28))
                .size(50.dp)
        )
        VStack(
            spaceBy = 0.dp, modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Text(
                text = data.user?.fullName ?: "",
                fontWeight = SemiBold,
                fontSize = 14.sp,
                fontFamily = outFit,
                color = MineShaft,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = data.text ?: "",
                fontSize = 10.sp,
                fontFamily = outFit,
                fontWeight = Medium,
                color = black94,
                textAlign = TextAlign.Justify,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = AppUtils.formatTimestampForComments(data.createdAt ?: 0),
            fontWeight = Normal,
            fontSize = 12.sp,
            fontFamily = outFit,
            color = ColorOsloGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
