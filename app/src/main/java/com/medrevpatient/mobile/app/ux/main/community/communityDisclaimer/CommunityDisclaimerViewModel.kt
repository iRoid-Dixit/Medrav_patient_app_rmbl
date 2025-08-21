package com.medrevpatient.mobile.app.ux.main.community.communityDisclaimer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.utils.AppUtils


import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDisclaimerViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(CommunityDisclaimerUiState())
    val uiState = _uiState.asStateFlow()

    fun event(event: CommunityDisclaimerUiEvent) {
        when (event) {
            is CommunityDisclaimerUiEvent.PerformAgreeToGuidelinesClick -> {
                viewModelScope.launch {
                    if (_uiState.value.readGuidelinesCheckmark == 0) {
                        AppUtils.Toast(context, context.getString(R.string.please_read_community_guidelines)).show()
                        return@launch
                    }
                    acceptGuideLineAPI()
                }
            }

            is CommunityDisclaimerUiEvent.ReadGuidelinesCheckmark -> {
                _uiState.update { state ->
                    state.copy(
                        readGuidelinesCheckmark = event.value
                    )
                }
            }
        }
    }

    private fun acceptGuideLineAPI() {
        viewModelScope.launch {
            apiRepository.acceptCommunityGuideline().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        storeUserData(coroutineScope = viewModelScope, userAuthResponse = it.data?.data)
                    }
                }
            }
        }
    }

    fun navigate(navRoute: NavRoute) {
        navigate(NavigationAction.Navigate(navRoute))
    }

    private fun showOrHideLoader(isLoading: Boolean) {
        _uiState.update { state ->
            state.copy(
                isLoading = isLoading
            )
        }
    }

    private fun storeUserData(coroutineScope: CoroutineScope, userAuthResponse: AuthResponse?) {
        coroutineScope.launch {
            val tokenData = localManager.retrieveUserData()?.auth
            if (tokenData != null) {
                localManager.saveUserTokenData(tokenData)
            }
            userAuthResponse?.let { data ->
                data.auth = tokenData
                localManager.saveUserData(data)
            }
            this@CommunityDisclaimerViewModel.popBackStack()
        }
    }
}


data class CommunityDisclaimerUiState(
    val event: (CommunityDisclaimerUiEvent) -> Unit = {},
    val userData: StateFlow<AuthResponse> = MutableStateFlow(AuthResponse()),
    val isLoading: Boolean = false,
    val readGuidelinesCheckmark: Int = 0
)

sealed interface CommunityDisclaimerUiEvent {
    data object PerformAgreeToGuidelinesClick : CommunityDisclaimerUiEvent
    data class ReadGuidelinesCheckmark(val value: Int) : CommunityDisclaimerUiEvent
}