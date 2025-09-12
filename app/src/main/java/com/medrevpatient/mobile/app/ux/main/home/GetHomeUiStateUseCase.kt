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
import com.medrevpatient.mobile.app.model.domain.request.bmi.BmiCalculateRequest
import com.medrevpatient.mobile.app.model.domain.request.report.ReportUserPostReq
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse

import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.model.domain.response.home.HomeScreenData
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.ux.container.ContainerActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetHomeUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    private val homePatientData = MutableStateFlow(HomeScreenData())
    private val homeUiDataFlow = MutableStateFlow(HomeUiDataState())
    private lateinit var context: Context
    operator fun invoke(
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): HomeUiState {
        getHomePatientData(
            coroutineScope = coroutineScope,
            navigate = navigate
        )
        coroutineScope.launch {
            homeUiDataFlow.update { state ->
                state.copy(
                    userName = "${appPreferenceDataStore.getUserData()?.firstName ?: ""} ${appPreferenceDataStore.getUserData()?.lastName ?: ""}".trim(),
                    userProfile = appPreferenceDataStore.getUserData()?.profileImage ?: ""
                )
            }
        }
        coroutineScope.launch {
            Log.d("TAG", "lastName: ${appPreferenceDataStore.getUserData()?.firstName},${appPreferenceDataStore.getUserData()?.lastName}")
        }
        return HomeUiState(
            homeUiDataFlow = homeUiDataFlow,
            homePatientData = homePatientData,
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
            is HomeUiEvent.GetContext -> {
                this.context = event.context
            }
            HomeUiEvent.NotificationClick -> {
                navigateToContainerScreens(context, navigate, screenName = Constants.AppScreen.NOTIFICATION_SCREEN)
            }
            HomeUiEvent.DailyDietClick -> {
                navigateToContainerScreens(context, navigate, screenName = Constants.AppScreen.DAILY_DIET_CHALLENGE_SCREEN)
            }
            HomeUiEvent.SideEffectClick -> {
                navigateToContainerScreens(context, navigate, screenName = Constants.AppScreen.SIDE_EFFECT_CHECK_SCREEN)
            }
            HomeUiEvent.CalculateBMIClick -> {
                navigateToContainerScreens(context, navigate, screenName = Constants.AppScreen.CALCULATE_BMI_SCREEN)
            }
            HomeUiEvent.GetDataFromPref -> {
                coroutineScope.launch {
                    homeUiDataFlow.update { state ->
                        state.copy(
                            userName = "${appPreferenceDataStore.getUserData()?.firstName ?: ""} ${appPreferenceDataStore.getUserData()?.lastName ?: ""}".trim(),
                            userProfile = appPreferenceDataStore.getUserData()?.profileImage ?: ""
                        )
                    }
                }
            }
        }
    }

    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        navigate(NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun getHomePatientData(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            try {
                apiRepository.getPatientHomeScreenData().collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showErrorMessage(context = context, it.message ?: "Something went wrong!")
                            Log.d("TAG", "getHomePatientData: ${it.message}")
                            showOrHideLoader(false)
                        }
                        is NetworkResult.Loading -> {
                            showOrHideLoader(true)
                        }
                        is NetworkResult.Success -> {
                            showOrHideLoader(false)
                            // Update homePatientData with the API response
                            val responseData = it.data?.data
                            if (responseData != null) {
                                homePatientData.value = responseData
                                Log.d("TAG", "HomeScreenData updated: displaySideEffectCheckIn=${responseData.displaySideEffectCheckIn}, displayDailyDietChallenge=${responseData.displayDailyDietChallenge}")
                            }
                            showSuccessMessage(context = context, it.data?.message ?: "Data loaded successfully")
                        }
                        is NetworkResult.UnAuthenticated -> {
                            showOrHideLoader(false)
                            showErrorMessage(context = context, it.message ?: "Authentication failed!")
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle any unexpected errors
                showOrHideLoader(false)
                showErrorMessage(context = context, "An unexpected error occurred: ${e.message}")
            }
        }
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        homeUiDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

}