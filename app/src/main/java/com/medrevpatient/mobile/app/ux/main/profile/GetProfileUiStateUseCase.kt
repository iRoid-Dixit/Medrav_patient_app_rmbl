package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.authReq.LogoutReq
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.startup.StartupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetProfileUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    private val profileUiDataFlow = MutableStateFlow(ProfileUiDataState())
    private lateinit var context: Context
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ProfileUiState {
        return ProfileUiState(
            messageUiDataFlow = profileUiDataFlow,
            event = { profileUiEvent ->
                profileUiEvent(
                    event = profileUiEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }
        )
    }
    private fun profileUiEvent(
        event: ProfileUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            ProfileUiEvent.EditProfile -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.EDIT_PROFILE_SCREEN
                )
            }
            ProfileUiEvent.ChangePassword -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.CHANGE_PASSWORD_SCREEN
                )
            }
            ProfileUiEvent.DeleteAccount -> {
                profileUiDataFlow.update {
                    it.copy(
                        deleteSheetVisible = true
                    )
                }
            }
            ProfileUiEvent.Logout -> {
                profileUiDataFlow.update {
                    it.copy(
                        logoutSheetVisible = true
                    )
                }
            }
            ProfileUiEvent.CustomerService -> {

            }
            is ProfileUiEvent.LogoutSheetVisibility -> {
                profileUiDataFlow.update { state ->
                    state.copy(
                        logoutSheetVisible = event.isVisible
                    )
                }
            }
            ProfileUiEvent.LogoutAPICall -> {
                logout(
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
            is ProfileUiEvent.DeleteSheetVisibility -> {
                profileUiDataFlow.update { state ->
                    state.copy(
                        deleteSheetVisible = event.isVisible
                    )
                }
            }
            ProfileUiEvent.DeleteAPICall -> {
                // TODO: delete api call 
                
            }

            is ProfileUiEvent.GetContext -> {
                this.context = event.context
            }
        }
    }

    private fun logout(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
                val logoutReq = LogoutReq(
                    refresh = appPreferenceDataStore.getUserAuthData()?.accessToken?:""
                )
                apiRepository.doLogout(logoutReq).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showErrorMessage(
                                context =  this@GetProfileUiStateUseCase.context,
                                it.message ?: "Something went wrong!"
                            )
                            showOrHideLoader(false)
                        }

                        is NetworkResult.Loading -> {
                            showOrHideLoader(true)
                        }

                        is NetworkResult.Success -> {
                            showOrHideLoader(false)
                            appPreferenceDataStore.clearAll()
                            showSuccessMessage(
                                context = this@GetProfileUiStateUseCase.context,
                                it.data?.message ?: ""
                            )
                            coroutineScope.launch {
                                delay(1000)
                                appPreferenceDataStore.clearAll()
                                val intent = Intent(context, StartupActivity::class.java)
                                intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.SIGN_IN)
                                navigate(
                                    NavigationAction.NavigateIntent(
                                        intent,
                                        finishCurrentActivity = true
                                    ),
                                )
                            }
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showErrorMessage(
                                context = this@GetProfileUiStateUseCase.context,
                                it.message ?: "Something went wrong!"
                            )
                            showOrHideLoader(false)
                        }
                    }
                }

        }
    }
    private fun showOrHideLoader(showLoader: Boolean) {
        profileUiDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
}


