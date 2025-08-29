package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetProfileUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    private val settingUiDataFlow = MutableStateFlow(ProfileUiDataState())
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ProfileUiState {
        return ProfileUiState(
            messageUiDataFlow = settingUiDataFlow,
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
                // Navigate to edit profile screen
                // navigate(NavigationAction.NavigateToEditProfile)
            }
            ProfileUiEvent.ChangePassword -> {
                // Navigate to change password screen
                // navigate(NavigationAction.NavigateToChangePassword)
            }
            ProfileUiEvent.DeleteAccount -> {
                // Show delete account confirmation dialog
                // For now, just log the action
            }
            ProfileUiEvent.Logout -> {
                // Handle logout logic
                // Clear user data and navigate to login
                // navigate(NavigationAction.NavigateToLogin)
            }
        }
    }
}


