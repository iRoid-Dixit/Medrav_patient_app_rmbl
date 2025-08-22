package com.medrevpatient.mobile.app.ux.main.griotLegacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.imageDisplay.ImageDisplayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
class GetGriotLegacyUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val griotLegacyUiDataState = MutableStateFlow(GriotLegacyUiDataState())
    private val legacyPostList =
        MutableStateFlow<PagingData<LegacyPostResponse>>(PagingData.empty())
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): GriotLegacyUiState {
        getLegacyPostData(coroutineScope = coroutineScope, 0)
        return GriotLegacyUiState(
            griotLegacyUiDataFlow = griotLegacyUiDataState,
            allLegacyPostListFlow = legacyPostList,
            event = { griotLegacy ->
                griotLegacyUiEvent(
                    context = context,
                    event = griotLegacy,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun griotLegacyUiEvent(
        context: Context,
        event: GriotLegacyUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is GriotLegacyUiEvent.PullToRefreshAPICall -> {
                getLegacyPostData(coroutineScope, griotLegacyUiDataState.value.tabIndex)
            }

            is GriotLegacyUiEvent.TabClick -> {
                griotLegacyUiDataState.update { state ->
                    state.copy(
                        tabIndex = event.type
                    )
                }
                getLegacyPostData(coroutineScope, griotLegacyUiDataState.value.tabIndex)
            }

            is GriotLegacyUiEvent.PostDetailsClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    postId = event.postId,
                    screenName = Constants.AppScreen.POST_DETAILS_SCREEN
                )
            }

            GriotLegacyUiEvent.BuildLegacyClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    postId = "",
                    screenName = Constants.AppScreen.BUILD_LEGACY_SCREEN
                )
            }

            is GriotLegacyUiEvent.VideoPreviewClick -> {
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
            is GriotLegacyUiEvent.ImageDisplay -> {
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
            GriotLegacyUiEvent.NavigateToNotification -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    postId = "",
                    screenName = Constants.AppScreen.NOTIFICATION_SCREEN
                )
            }
        }
    }
    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        postId: String,
        screenName: String,
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.POST_ID, postId)
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        intent.putExtra(Constants.IS_FORM, bundle)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun getLegacyPostData(coroutineScope: CoroutineScope, type: Int) {
        coroutineScope.launch {
            apiRepository.getLegacyPost(type).cachedIn(coroutineScope).collect { pagingData ->
                legacyPostList.value = pagingData
            }
        }
    }
}