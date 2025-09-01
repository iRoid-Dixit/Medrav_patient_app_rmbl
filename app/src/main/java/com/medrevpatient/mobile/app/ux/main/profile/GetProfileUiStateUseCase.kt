package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GetProfileUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    private val profileUiDataFlow = MutableStateFlow(ProfileUiDataState())
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
                // TODO: logout api call
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


