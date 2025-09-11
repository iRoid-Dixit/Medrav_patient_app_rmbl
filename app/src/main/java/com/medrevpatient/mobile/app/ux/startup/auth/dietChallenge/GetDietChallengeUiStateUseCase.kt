package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import android.content.Context
import android.content.Intent
import android.util.Log
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.dietChallenge.DietChallengeSubmitRequest
import com.medrevpatient.mobile.app.model.domain.response.dietChallenge.DietChallengeResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
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

class GetDietChallengeUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,

    ) {
    private val dietChallengeDataFlow = MutableStateFlow(DietChallengeData())
    private val dietChallengeData = MutableStateFlow(DietChallengeResponse())
    private val isOffline = MutableStateFlow(false)
    private var currentFoodId: Int? = null
    private lateinit var context: Context

    private var isInitialDataLoaded = false

    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): DietChallengeUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        // Only call API if initial data is not loaded
        if (!isInitialDataLoaded) {
            Log.d("TAG", "Loading initial diet challenge data...")
            getDietChallengeData(
                coroutineScope = coroutineScope,
                navigate = navigate
            )
        } else {
            Log.d("TAG", "Initial data already loaded, skipping API call")
        }
        return DietChallengeUiState(
            dietChallengeDataFlow = dietChallengeDataFlow,
            dietListData = dietChallengeData,
            event = { dietChallengeUiEvent ->
                dietChallengeEvent(
                    event = dietChallengeUiEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                )
            }
        )
    }

    private fun dietChallengeEvent(
        event: DietChallengeUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        when (event) {
            is DietChallengeUiEvent.GetContext -> {
                this.context = event.context
            }

            is DietChallengeUiEvent.SelectCategory -> {
                // Find the selected category ID
                val selectedCategory = dietChallengeData.value.availableCategories?.find {
                    it?.name == event.category
                }
                val categoryId = selectedCategory?.id

                // Update the dietChallengeDataFlow with selected category
                dietChallengeDataFlow.update { state ->
                    state.copy(selectedCategoryId = categoryId)
                }
            }

            is DietChallengeUiEvent.ContinueChallenge -> {
                // Submit the answer before navigating
                submitAnswer(
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )

            }

            DietChallengeUiEvent.OnBackClick -> {
                navigate(PopIntent)
            }
            
            DietChallengeUiEvent.ResetChallenge -> {
                // Reset the challenge state
                isInitialDataLoaded = false
                dietChallengeDataFlow.value = DietChallengeData()
                dietChallengeData.value = DietChallengeResponse()
                // Get fresh data
                getDietChallengeData(coroutineScope, navigate)
            }
        }
    }
    private fun getDietChallengeData(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.getDietChallenge().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        Log.d("TAG", "getDietChallengeData Error: ${it.message}")
                        showOrHideLoader(false)

                    }
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        val responseData = it.data?.data
                        dietChallengeData.value = responseData ?: DietChallengeResponse()
                        currentFoodId = responseData?.currentQuestion?.foodId
                        // Update the dietChallengeDataFlow with the response data
                        dietChallengeDataFlow.update { state ->
                            state.copy(
                                correctAnswers = responseData?.correctAnswers ?: 0,
                                incorrectAnswers = responseData?.incorrectAnswers ?: 0,
                                questionsRemaining = responseData?.questionsRemaining,
                                isCompleted = responseData?.isCompleted ?: false,
                                selectedCategoryId = null // Reset selection for new question
                            )
                        }
                        isInitialDataLoaded = true
                        showSuccessMessage(context = context, it.data?.message ?: "Data loaded successfully")
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Authentication failed!")
                    }
                }
            }
        }
    }
    private fun showOrHideLoader(showLoader: Boolean) {
        dietChallengeDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
    private fun submitAnswer(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val selectedCategoryId = dietChallengeDataFlow.value.selectedCategoryId
            val request = DietChallengeSubmitRequest(
                foodId = currentFoodId ?: 0,
                selectedCategoryId = selectedCategoryId ?: 0
            )
            apiRepository.submitDietChallengeAnswer(request).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        val responseData = result.data?.data
                        dietChallengeDataFlow.update { state ->
                            state.copy(
                                correctAnswers = responseData?.correctAnswers ?: 0,
                                incorrectAnswers = responseData?.incorrectAnswers ?: 0,
                                questionsRemaining = responseData?.questionsRemaining,
                                isCompleted = responseData?.isCompleted == true,
                                selectedCategoryId = null // Reset selection for next question
                            )
                        }
                        dietChallengeData.value = responseData ?: DietChallengeResponse()
                        currentFoodId = responseData?.currentQuestion?.foodId
                        showSuccessMessage(context, result.data?.message ?: "Answer submitted successfully!")
                        if (responseData?.isCompleted == true) {
                           // navigate(PopIntent)
                            navigateToMainActivityScreens(
                                context = context,
                                navigate = navigate,
                                screenName = Constants.AppScreen.HOME_SCREEN
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                            showErrorMessage(context, result.message ?: "Something went wrong!")
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun navigateToMainActivityScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        navigate(NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

}