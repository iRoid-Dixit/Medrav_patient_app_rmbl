package com.medrevpatient.mobile.app.ux.startup.auth.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.BuildConfig
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.authReq.AppUpdateRequest
import com.medrevpatient.mobile.app.model.domain.response.auth.AppUpdateResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWarningMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWarningMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.startup.auth.bmi.BmiRoute
import com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge.DietChallengeRoute
import com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion.SideEffectQuestionRoute
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginRoute
import com.medrevpatient.mobile.app.ux.startup.auth.weightTracker.WeightTrackerRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSplashUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private val bundle = MutableStateFlow<Bundle>(Bundle.EMPTY)
    private val splashUiDataFlow = MutableStateFlow(SplashUiData())
    private lateinit var context: Context
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SplashUiState {

        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return SplashUiState(
            splashUiDataFlow = splashUiDataFlow,
            event = { authUiEvent ->
                authEvent(
                    event = authUiEvent,
                    coroutineScope = coroutineScope,
                    navigate = navigate

                )
            }
        )
    }

    private fun navigateToNextScreen(
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        context: Context
    ) {
        coroutineScope.launch {
            delay(3000)
            if (!isOffline.value) {
                coroutineScope.launch {
                    val userData = appPreferenceDataStore.getUserData()
                    if (userData != null) {
                        val intent = Intent(context, MainActivity::class.java)
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    } else {
                        navigate(NavigationAction.PopAndNavigate(LoginRoute.createRoute()))
                    }
                }
            } else {
               AppUtils.showWarningMessage(
                    this@GetSplashUiStateUseCase.context,
                    "Please check your internet connection!"
                )
            }
        }
    }

    private fun authEvent(
        event: SplashUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit

    ) {
        when (event) {
            is SplashUiEvent.GetContext -> {
                this.context = event.context
            }

            is SplashUiEvent.GetIntentData -> {
                bundle.value = event.bundle
                val notificationType =
                    bundle.value.getString(Constants.BundleKey.NOTIFICATION_TYPE) ?: ""
                val postId =
                    bundle.value.getString(Constants.BundleKey.POST_ID) ?: ""
                if (notificationType.isNotEmpty()) {
                    managePushIntent(
                        notificationType = notificationType,
                        context = context,
                        navigate = navigate,
                        postId = postId
                    )
                }
            }

            is SplashUiEvent.RestartAppKey -> {
                if (event.key == Constants.BundleKey.RESTART_APP) {
                    clearAllPrefData(coroutineScope = coroutineScope, navigate = navigate)
                } else {
                    //checkAppUpdate(coroutineScope = coroutineScope, context = context, navigate = navigate)
                    navigateToNextScreen(navigate, coroutineScope, context)
                }
            }

            SplashUiEvent.OnClickOfCancel -> {

                splashUiDataFlow.update { state ->
                    state.copy(
                        showAppUpdateDialog = false
                    )

                }
                navigateToNextScreen(navigate, coroutineScope, context)

            }

            is SplashUiEvent.OnClickOfUpdate -> {
                moveToPlayStore(
                    context = event.appContext,
                    appLink = splashUiDataFlow.value.appLink
                )
                splashUiDataFlow.update { state ->
                    state.copy(
                        isForceUpdate = false,
                        showAppUpdateDialog = false
                    )
                }
                navigateToNextScreen(
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    context = event.appContext
                )
            }
        }
    }

    private fun moveToPlayStore(context: Context, appLink: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(appLink.ifEmpty { "https://play.google.com/store" })
        )
        context.startActivity(browserIntent)
    }

    private fun managePushIntent(
        notificationType: String,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        postId: String,
    ) {
        if (notificationType.isNotEmpty()) {
            try {
                when (notificationType.toInt()) {
                    Constants.NotificationPush.LIKE_DISLIKE_TYPE -> {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra(
                            Constants.IS_COME_FOR,
                            Constants.AppScreen.MAIN_VILLAGE_SCREEN
                        )
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    }

                    Constants.NotificationPush.COMMENT_TYPE -> {
                        val bundle = Bundle()
                        val intent = Intent(context, ContainerActivity::class.java)
                        intent.putExtra(
                            Constants.IS_COME_FOR,
                            Constants.AppScreen.POST_DETAILS_SCREEN
                        )
                        intent.putExtra(Constants.IS_FORM, bundle)
                        bundle.putString(Constants.BundleKey.POST_ID, postId)
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    }

                    Constants.NotificationPush.ADD_POST -> {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra(
                            Constants.IS_COME_FOR,
                            Constants.AppScreen.GRIOT_LEGACY_SCREEN
                        )
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    }

                    Constants.NotificationPush.MESSAGE -> {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.MESSAGE_SCREEN)
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    }

                    else -> {
                        val intent = MainActivity.newIntent(context)
                        navigate(
                            NavigationAction.NavigateIntent(
                                intent = intent,
                                finishCurrentActivity = true
                            )
                        )
                    }
                }
            } catch (_: NumberFormatException) {
                // Handle case where notificationType is not a valid number
                val intent = Intent(context, MainActivity::class.java)
                navigate(
                    NavigationAction.NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = true
                    )
                )
            }
        } else {
            val intent = Intent(context, MainActivity::class.java)
            navigate(
                NavigationAction.NavigateIntent(
                    intent = intent,
                    finishCurrentActivity = true
                )
            )
        }
    }

    private fun checkAppUpdate(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val appUpdateRequest = AppUpdateRequest(
                version = BuildConfig.VERSION_NAME,
                type = Constants.Subscription.ANDROID
            )
            apiRepository.checkAppUpdate(appUpdateRequest = appUpdateRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        Logger.e("checkSubscription Error: ${it.message}")
                    }

                    is NetworkResult.Loading -> {
                        Logger.e("checkSubscription Loading")
                    }

                    is NetworkResult.Success -> {
                        val appUpdateResponse = it.data?.data
                        Log.d("TAG", "checkAppUpdate: $appUpdateResponse")
                        checkStatusAndMoveAhead(
                            appUpdateResponse = appUpdateResponse,
                            context = context,
                            coroutineScope = coroutineScope,
                            navigate = navigate
                        )
                    }

                    is NetworkResult.UnAuthenticated -> {
                        navigate(NavigationAction.PopAndNavigate(LoginRoute.createRoute()))
                    }
                }
            }
        }
    }

    private fun checkStatusAndMoveAhead(
        appUpdateResponse: AppUpdateResponse?,
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        if (appUpdateResponse != null) {
            val appStatus = appUpdateResponse.response
            Log.d("TAG", "checkStatusAndMoveAhead: $appStatus")
            when (appStatus) {
                Constants.AppStatus.UP_TO_DATE -> {
                    navigateToNextScreen(navigate, coroutineScope, context)
                }

                Constants.AppStatus.FORCE_UPDATE -> {
                    splashUiDataFlow.update { state ->
                        state.copy(
                            showAppUpdateDialog = true,
                            isForceUpdate = true,
                            appLink = appUpdateResponse.appLink ?: "",
                            appMessage = "A new version is available. Please update to continue."
                        )
                    }
                }

                Constants.AppStatus.RECOMMEND_UPDATE -> {
                    splashUiDataFlow.update { state ->
                        state.copy(
                            showAppUpdateDialog = true,
                            isForceUpdate = false,
                            appLink = appUpdateResponse.appLink ?: "",
                            appMessage = "A new version is available. We recommend updating for better performance."
                        )
                    }
                }

                else -> {
                    // Default case - navigate to next screen
                    navigateToNextScreen(navigate, coroutineScope, context)
                }
            }
        } else {
            showErrorMessage(
                context = context,
                message = "Something went wrong, Please relaunch the app!"
            )
        }
    }

    private fun clearAllPrefData(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            appPreferenceDataStore.clearAll()
            navigate(NavigationAction.PopAndNavigate(LoginRoute.createRoute()))
        }
    }


}