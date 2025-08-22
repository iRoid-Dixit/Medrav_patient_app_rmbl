package com.medrevpatient.mobile.app.ux.main.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.report.ReportUserPostReq
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.imageDisplay.ImageDisplayActivity
import com.medrevpatient.mobile.app.ux.main.griotLegacy.GriotLegacyRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetHomeUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,

    ) {
    private val homeUiDataFlow = MutableStateFlow(HomeUiDataState())
    private lateinit var context: Context
    private var userData: UserAuthResponse? = null
    private val mainVillageList =
        MutableStateFlow<PagingData<LegacyPostResponse>>(PagingData.empty())
    private val advertisementList =
        MutableStateFlow<PagingData<AdvertisementResponse>>(PagingData.empty())

    operator fun invoke(
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): HomeUiState {
        getMainVillagePage(coroutineScope = coroutineScope)
        getGetAdvertisements(coroutineScope = coroutineScope)
        coroutineScope.launch {
            userData = appPreferenceDataStore.getUserData()
            userData?.let {
                homeUiDataFlow.update { postDetailsDataFlow ->
                    postDetailsDataFlow.copy(
                        userId = it.id ?: "",
                    )
                }
            }
        }
        return HomeUiState(
            homeUiDataFlow = homeUiDataFlow,
            mainVillageList = mainVillageList,
            advertisementList = advertisementList,
            event = { homeUiEvent ->
                homeUiEvent(
                    coroutineScope = coroutineScope,
                    event = homeUiEvent,
                    navigate = navigate,

                )
            }
        )
    }

    private fun homeUiEvent(
        coroutineScope: CoroutineScope,
        event: HomeUiEvent,
        navigate: (NavigationAction) -> Unit,


    ) {
        when (event) {
            is HomeUiEvent.IsLikeDisLikeAPICall -> {
                likeDislikeAPICall(
                    coroutineScope = coroutineScope,
                    likeDislikeId = event.likeDislikeId
                )
            }

            is HomeUiEvent.GetContext -> {
                this.context = event.context
            }

            HomeUiEvent.PullToRefreshAPICall -> {
                getMainVillagePage(coroutineScope = coroutineScope)
                getGetAdvertisements(coroutineScope)
            }

            is HomeUiEvent.NavigateToPostDetails -> {
                homeUiDataFlow.update { state ->
                    state.copy(
                        isRefreshData = false
                    )
                }
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    postId = event.postId,
                )
            }

            is HomeUiEvent.VideoPreviewClick -> {
                val bundle = Bundle()
                val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IS_FORM, bundle)
                bundle.putString(Constants.BundleKey.MEDIA_LIST, Gson().toJson(event.mediaList))
                navigate(
                    NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = false
                    )
                )
            }

            is HomeUiEvent.ImageDisplay -> {
                val bundle = Bundle()
                val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IS_FORM, bundle)
                bundle.putString(Constants.BundleKey.MEDIA_LIST, Gson().toJson(event.mediaList))
                navigate(
                    NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = false
                    )
                )
            }

            is HomeUiEvent.ReportPost -> {
                doReportUserAndPost(
                    coroutineScope = coroutineScope,
                    type = Constants.ReportType.REPORT_POST,
                    id = event.postId
                )
            }

            is HomeUiEvent.ReportUser -> {
                doReportUserAndPost(
                    coroutineScope = coroutineScope,
                    type = Constants.ReportType.REPORT_USER,
                    id = event.userId
                )
            }

            HomeUiEvent.NavigateToNotification -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                )
            }
            is HomeUiEvent.PostId -> {
                homeUiDataFlow.update { state ->
                    state.copy(
                        postId = event.postId
                    )
                }
            }
            HomeUiEvent.UpdateVillageList -> {
                mainVillageList.update { pagingData ->
                    pagingData.filter { it.id != homeUiDataFlow.value.postId }
                }
            }
            is HomeUiEvent.RefreshData -> {
                homeUiDataFlow.update { state ->
                    state.copy(
                        isRefreshData = event.refresh
                    )
                }
            }
            HomeUiEvent.BackClick -> {
                navigate(Navigate(GriotLegacyRoute.createRoute()))
            }

            is HomeUiEvent.AdvertisementClick -> {
                advertisementViewAPICall(
                    coroutineScope = coroutineScope,
                    advertisementId = event.advertisementId,

                    )
            }
        }
    }
    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.NOTIFICATION_SCREEN)
        navigate(NavigateIntent(intent = intent, finishCurrentActivity = false))
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
                        mainVillageList.update { pagingData ->
                            pagingData.map { post ->
                                if (post.id == likeDislikeId) {
                                    post.copy(
                                        likeCount = post.likeCount?.let { count ->
                                            if (post.ownLike == true) count - 1 else count + 1
                                        },
                                        ownLike = post.ownLike != true
                                    )
                                } else {
                                    post
                                }
                            }
                        }
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
    private fun advertisementViewAPICall(coroutineScope: CoroutineScope, advertisementId: String) {
        coroutineScope.launch {
            apiRepository.getAdvertisementView(advertisementId = advertisementId).collect {
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
                        val advertisementUrl = it.data?.data?.link ?: ""
                        Log.d("BrowserIntent", "Advertisement URL from API: '$advertisementUrl'")
                        Log.d("TAG", "advertisementViewAPICall: ${it.data?.data?.link}")
                        openTermsAndConditionsInBrowser(context = context, url = advertisementUrl)
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

    private fun openTermsAndConditionsInBrowser(context: Context, url: String) {
        Log.d("BrowserIntent", "Processing URL: '$url'")
        try {
            // Simple validation
            if (url.isBlank()) {
                Log.e("BrowserIntent", "URL is blank or empty")
                return
            }
            // Clean the URL - just trim whitespace
            val cleanedUrl = url.trim()
            Log.d("BrowserIntent", "Cleaned URL: '$cleanedUrl'")
            // Simple URL processing - just add https if missing
            val finalUrl = when {
                cleanedUrl.startsWith("http://") || cleanedUrl.startsWith("https://") -> cleanedUrl
                cleanedUrl.startsWith("www.") -> "https://$cleanedUrl"
                else -> "https://$cleanedUrl"
            }
            Log.d("BrowserIntent", "Final URL: '$finalUrl'")
            // Simple intent creation - like Chrome does
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                // Just try to open it - let the system handle any issues
                context.startActivity(intent)
                Log.d("BrowserIntent", "Successfully opened URL: $finalUrl")

            } catch (e: Exception) {
                Log.e("BrowserIntent", "Error opening URL: $finalUrl", e)
                // Try fallback with just the original URL
                try {
                    val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanedUrl)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(fallbackIntent)
                    Log.d("BrowserIntent", "Fallback successful for URL: $cleanedUrl")
                } catch (e2: Exception) {
                    Log.e("BrowserIntent", "Fallback also failed for URL: $cleanedUrl", e2)
                }
            }

        } catch (e: Exception) {
            Log.e("BrowserIntent", "Error processing URL: $url", e)
        }
    }
    private fun getGetAdvertisements(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getHomeAdvertisements().cachedIn(this).collect { pagingData ->
                advertisementList.value = pagingData
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
    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        postId: String,
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.POST_ID, postId)
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.MAIN_VILLAGE_SCREEN)
        intent.putExtra(Constants.IS_FORM, bundle)
        navigate(
            NavigateIntent(
                intent = intent,
                finishCurrentActivity = false
            )
        )
        // launcher.launch(intent)
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        homeUiDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun getMainVillagePage(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getMainVillage().cachedIn(this).collect { pagingData ->
                mainVillageList.value = pagingData
            }
        }
    }

}