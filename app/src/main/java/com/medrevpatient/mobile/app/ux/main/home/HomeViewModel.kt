package com.medrevpatient.mobile.app.ux.main.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.response.HomeScreenResponse
import com.medrevpatient.mobile.app.navigation.NavRoute
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.startup.StartupActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getUserData()
    }

    fun event(event: HomeUIEvent) {
        when (event) {
            is HomeUIEvent.PerformLogoutClick -> {
                callLogoutApi()
            }

            is HomeUIEvent.PerformProfileClick -> {
                navigate(NavigationAction.Navigate(RouteMaker.Profile.createRoute()))
            }

            is HomeUIEvent.PerformPinDataClick -> {
                if (uiState.value.homeData.pinData?.type == 1) {
                    navigate(NavigationAction.Navigate(RouteMaker.ViewProgram.createRoute(event.id)))
                } else {
                    navigate(NavigationAction.Navigate(RouteMaker.ViewRecipe.createRoute(event.id)))
                }
            }

            is HomeUIEvent.PerformPopularClick -> {
                if (event.type == 1) {
                    navigate(NavigationAction.Navigate(RouteMaker.ViewProgram.createRoute(event.id)))
                } else {
                    navigate(NavigationAction.Navigate(RouteMaker.ViewRecipe.createRoute(event.id)))
                }
            }

            is HomeUIEvent.PerformNotificationPermissionCancelClick -> {
                _uiState.update {
                    it.copy(isCancelClickForNotificationPermission = event.value)
                }
                viewModelScope.launch {
                    localManager.savePushNotificationPermission(event.value)
                }
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            val needToOpenNotificationPermission = localManager.getPushNotificationPermission()
            val response = localManager.retrieveUserData()
            if (response != null) {
                _uiState.update {
                    it.copy(userData = response, isCancelClickForNotificationPermission = needToOpenNotificationPermission ?: false)
                }
            }
        }
    }

    private fun callLogoutApi() {
        viewModelScope.launch {
            apiRepository.logout().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        //showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        //showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        //showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        localManager.clearStorage()
                        val intent = Intent(context, StartupActivity::class.java)
                        val screen = RouteMaker.SignInRoute.routeDefinition.value
                        intent.putExtra(Constants.IntentKeys.NEED_TO_OPEN, screen)
                        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
                    }
                }
            }
        }
    }

    fun callHomeApi() {
        viewModelScope.launch {
            apiRepository.getHomeScreenData().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        //showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        //showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        //showOrHideLoader(false)
                        _uiState.update { it1 ->
                            it1.copy(homeData = it.data?.data ?: HomeScreenResponse())
                        }
                    }
                }
            }
        }
    }
    fun navigate(navRoute: NavRoute) {
        navigate(NavigationAction.Navigate(navRoute))
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val event: (HomeUIEvent) -> Unit = {},
    val userData: AuthResponse = AuthResponse(),
    val homeData: HomeScreenResponse = HomeScreenResponse(),
    val isCancelClickForNotificationPermission: Boolean = true
)

sealed interface HomeUIEvent {
    data object PerformLogoutClick : HomeUIEvent
    data object PerformProfileClick : HomeUIEvent
    data class PerformPinDataClick(val id: String) : HomeUIEvent
    data class PerformPopularClick(val id: String, val type: Int) : HomeUIEvent
    data class PerformNotificationPermissionCancelClick(val value: Boolean) : HomeUIEvent
}