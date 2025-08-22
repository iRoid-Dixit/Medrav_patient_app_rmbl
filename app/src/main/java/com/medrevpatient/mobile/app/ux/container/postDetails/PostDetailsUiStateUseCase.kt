package com.medrevpatient.mobile.app.ux.container.postDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.paging.ApiCallback
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.mainReq.AddCommentReq
import com.medrevpatient.mobile.app.model.domain.request.report.ReportUserPostReq
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.model.domain.response.container.comment.CommentResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.buildLegacy.BuildLegacyRoute
import com.medrevpatient.mobile.app.ux.imageDisplay.ImageDisplayActivity
import com.medrevpatient.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostDetailsUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val commentList =
        MutableStateFlow<PagingData<CommentResponse>>(PagingData.empty())
    private val postDetailsData = MutableStateFlow(LegacyPostResponse())
    private val postDetailsDataFlow = MutableStateFlow(PostDetailsUsDataState())
    private var userData: UserAuthResponse? = null
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): PostDetailsUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        coroutineScope.launch {
            userData = appPreferenceDataStore.getUserData()
            userData?.let {
                postDetailsDataFlow.update { postDetailsDataFlow ->
                    postDetailsDataFlow.copy(
                        userId = it.id ?: "",
                    )
                }
            }
        }
        return PostDetailsUiState(
            postDetailsDataFlow = postDetailsDataFlow,
            postDetailsData = postDetailsData,
            commentList = commentList,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }
        )
    }

    private fun contactUsUiEvent(
        event: PostDetailsUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            PostDetailsUiEvent.BackClick -> {
                navigateToMainScreens(context = context, navigate = navigate)

            }
            is PostDetailsUiEvent.GetContext -> {
                this.context = event.context

            }
            is PostDetailsUiEvent.PostId -> {
                val postId = event.postId
                postDetailsDataFlow.update {
                    it.copy(
                        postId = postId
                    )
                }
                getPostDetails(coroutineScope = coroutineScope, postId = postId)
                getCommentAPICall(coroutineScope = coroutineScope, post = postId)
            }
            is PostDetailsUiEvent.VideoPreviewClick -> {
                val bundle = Bundle()
                val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IS_FORM, bundle)
                bundle.putString(Constants.BundleKey.MEDIA_LIST, Gson().toJson(event.mediaList))
                navigate(
                    NavigationAction.NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = false
                    )
                )
            }

            PostDetailsUiEvent.PullToRefreshAPICall -> {
                getPostDetails(
                    coroutineScope = coroutineScope,
                    postId = postDetailsDataFlow.value.postId
                )
                getCommentAPICall(
                    coroutineScope = coroutineScope,
                    postDetailsDataFlow.value.postId
                )

            }

            is PostDetailsUiEvent.SendMessageValueChange -> {
                postDetailsDataFlow.update {
                    it.copy(
                        message = event.message
                    )
                }
            }

            is PostDetailsUiEvent.SendMessage -> {
                if (postDetailsDataFlow.value.message.isNotEmpty()) {
                    addCommentAPICall(coroutineScope, keyboard = event.keyboard)

                } else {
                    AppUtils.showWaringMessage(
                        context = this.context,
                        message = context.getString(R.string.please_add_comment_text)
                    )
                }
            }

            is PostDetailsUiEvent.IsLikeDisLikeAPICall -> {
                likeDislikeAPICall(
                    coroutineScope = coroutineScope,
                    likeDislikeId = event.likeDislikeId
                )
            }

            is PostDetailsUiEvent.EditPostClick -> {
                val data = Gson().toJson(event.legacyPostData)
                navigate(
                    NavigationAction.Navigate(
                        BuildLegacyRoute.createRoute(
                            data,
                            screeName = if (postDetailsDataFlow.value.screenName == Constants.AppScreen.MAIN_VILLAGE_SCREEN) Constants.AppScreen.MAIN_VILLAGE_SCREEN else Constants.AppScreen.BUILD_LEGACY_SCREEN
                        )
                    )
                )
            }

            PostDetailsUiEvent.DeleteLegacyPostClick -> {
                deleteLegacyPost(
                    coroutineScope = coroutineScope,
                    navigate = navigate,
                )

            }

            is PostDetailsUiEvent.DeleteDialog -> {
                postDetailsDataFlow.update {
                    it.copy(
                        showDialog = event.show
                    )
                }
            }

            is PostDetailsUiEvent.CommentCount -> {
                postDetailsDataFlow.update {
                    it.copy(
                        commentCount = event.commentCount
                    )
                }
            }

            is PostDetailsUiEvent.LikeCount -> {
                postDetailsDataFlow.update {
                    it.copy(
                        likeCount = event.likeCount,
                        ownLike = event.ownLike
                    )
                }
            }

            is PostDetailsUiEvent.ScreenName -> {
                postDetailsDataFlow.update {
                    it.copy(
                        screenName = event.screenName
                    )
                }
            }

            is PostDetailsUiEvent.ImageDisplay -> {
                val bundle = Bundle()
                val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IS_FORM, bundle)
                bundle.putString(Constants.BundleKey.MEDIA_LIST, Gson().toJson(event.mediaList))
                navigate(
                    NavigationAction.NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = false
                    )
                )
            }

            is PostDetailsUiEvent.ReportPost -> {
                doReportUserAndPost(
                    coroutineScope = coroutineScope,
                    type = Constants.ReportType.REPORT_POST,
                    id = event.postId
                )
            }

            is PostDetailsUiEvent.ReportUser -> {
                doReportUserAndPost(
                    coroutineScope = coroutineScope,
                    type = Constants.ReportType.REPORT_USER,
                    id = event.userId
                )
            }
        }
    }

    private fun deleteLegacyPost(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ) {
        coroutineScope.launch {
            apiRepository.deleteLegacyPost(postId = postDetailsDataFlow.value.postId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        postDetailsDataFlow.value.showDialog = false
                        showOrHideLoader(false)
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        coroutineScope.launch {
                            delay(1000)
                            navigateToMainScreens(context = context, navigate = navigate)
                        }

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }
    private fun navigateToMainScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.MAIN_VILLAGE_SCREEN)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun getPostDetails(coroutineScope: CoroutineScope, postId: String) {
        coroutineScope.launch {
            apiRepository.getPostDetails(postId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(
                            context = context,
                            it.data?.message ?: "Something went wrong!"
                        )
                        val postData = it.data?.data ?: LegacyPostResponse()
                        postDetailsData.value = postData
                        postDetailsDataFlow.update { state ->
                            state.copy(
                                likeCount = postData.likeCount ?: 0,
                                ownLike = postData.ownLike ?: false,
                                commentCount = postData.commentCount ?: 0
                            )
                        }
                        postDetailsData.value = it.data?.data ?: LegacyPostResponse()
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }

    private fun likeDislikeAPICall(coroutineScope: CoroutineScope, likeDislikeId: String) {
        coroutineScope.launch {
            apiRepository.likeDislike(postId = likeDislikeId).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(
                            context = context,
                            it.data?.message ?: "Something went wrong!"
                        )


                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }
        }

    }
    private fun doReportUserAndPost(
        coroutineScope: CoroutineScope,
        type: Int,
        id: String,

        ) {
        coroutineScope.launch {
            val reportUserPostReq = ReportUserPostReq(
                type = type,
                id = id
            )
            apiRepository.reportUserAndPost(reportUserPostReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun addCommentAPICall(
        coroutineScope: CoroutineScope,
        keyboard: SoftwareKeyboardController?
    ) {
        val addCommentReq = AddCommentReq(
            comment = postDetailsDataFlow.value.message,
        )
        coroutineScope.launch {
            apiRepository.addComment(
                postId = postDetailsDataFlow.value.postId,
                addCommentReq = addCommentReq
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(
                            context = context,
                            it.data?.message ?: "Something went wrong!"
                        )
                        getCommentAPICall(
                            coroutineScope = coroutineScope,
                            postDetailsDataFlow.value.postId
                        )
                        postDetailsDataFlow.update { it1 ->
                            it1.copy(
                                message = ""
                            )
                        }
                        keyboard?.hide()
                        showOrHideLoader(false)
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                    }
                }
            }
        }

    }


    private fun showOrHideLoader(showLoader: Boolean) {
        postDetailsDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun getCommentAPICall(coroutineScope: CoroutineScope, post: String) {
        coroutineScope.launch {
            apiRepository.getCommentList(post, apiCallback = object : ApiCallback() {
                override fun getMessage(message: String) {
                    // showSuccessMessage(this@PostDetailsUiStateUseCase.context,message)
                }
            }).cachedIn(coroutineScope).collect { pagingData ->
                commentList.value = pagingData

            }
        }
    }
}


