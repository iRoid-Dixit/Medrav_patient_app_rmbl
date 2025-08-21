package com.medrevpatient.mobile.app.ux.startup.subscription

import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSubsUiStateFlowUseCase
@Inject constructor(
    private val localManager: LocalManager,
    private val apiRepository: ApiRepository
) {
    private val subscriptionStateFlow = MutableStateFlow(SubscriptionUiState())
    private val showSubsDialog = MutableStateFlow(false)
    private val tempShowLogoutSheet = MutableStateFlow(false)
    private val tempLoader = MutableStateFlow(false)
    private val isMonthlySelected = MutableStateFlow(true)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SubsUiStateFlow {

        subscriptionStateFlow.update { subscriptionUiState ->
            subscriptionUiState.copy(
                listOfBenefits = arrayListOf(
                    "Personalized audio that speaks to you by name.",
                    "Guided prayer and affirmations.",
                    "Daily encouragement in pursuing your God-sized goal.",
                    "Help feed a hungry child in Africa!"
                )
            )
        }

        coroutineScope.launch {
            delay(1500L)
            showSubsDialog.value = true
        }

        return SubsUiStateFlow(
            subscriptionStateFlow = subscriptionStateFlow,
            showSubsDialog = showSubsDialog,
            tempShowLogoutSheet = tempShowLogoutSheet,
            tempLoader = tempLoader,
            event = { subscriptionUiEvent ->
                subscriptionEvent(context = context, event = subscriptionUiEvent, coroutineScope = coroutineScope, navigate = navigate)
            },
            isMonthlySelected = isMonthlySelected
        )
    }

    private fun subscriptionEvent(
        context: Context,
        event: SubscriptionUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is SubscriptionUiEvent.OnClickOfPlan -> {
                subscriptionStateFlow.update { subscriptionUiState ->
                    subscriptionUiState.copy(
                        activePlan = event.clickedPlan
                    )
                }
            }

            is SubscriptionUiEvent.ShowLogoutSheet -> {
                tempShowLogoutSheet.value = event.show
            }

            SubscriptionUiEvent.DoSubscribe -> {
                //move to home after subscription process
                val intent = Intent(context, MainActivity::class.java)
                navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
            }

            SubscriptionUiEvent.OnClickOfGotIt -> {
                showSubsDialog.value = false
            }

            SubscriptionUiEvent.DoLogOut -> {

            }

            is SubscriptionUiEvent.PerformSubscriptionTypeClick -> {
                isMonthlySelected.value = event.isMonthlySelected
            }

            SubscriptionUiEvent.StartFreeTrial -> {
                coroutineScope.launch {
                    val data = localManager.retrieveUserData()
                    if (data != null) {
                        //need to redirect to boarding section
                        navigate(NavigationAction.PopAndNavigate(RouteMaker.OnboardingRoute.createRoute()))
                        /*if (data.goals == null || data.goals == 0) {
                            //need to redirect to boarding section
                            navigate(NavigationAction.PopAndNavigate(RouteMaker.OnboardingRoute.createRoute()))
                        } else {
                            //boarding done, redirect to home screen
                            val intent = Intent(context, MainActivity::class.java)
                            navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
                        }*/
                    }
                }
            }
        }
    }

}