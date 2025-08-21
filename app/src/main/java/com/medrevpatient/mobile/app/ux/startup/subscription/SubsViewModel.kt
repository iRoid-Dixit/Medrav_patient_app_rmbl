package com.medrevpatient.mobile.app.ux.startup.subscription

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SubsViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSubsUiStateFlowUseCase: GetSubsUiStateFlowUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: SubsUiStateFlow = getSubsUiStateFlowUseCase(
        coroutineScope = viewModelScope,
        context = context
    ) { navigate(it) }
}


data class SubsUiStateFlow(
    val subscriptionStateFlow: StateFlow<SubscriptionUiState?> = MutableStateFlow(null),
    val tempShowLogoutSheet: StateFlow<Boolean> = MutableStateFlow(false),
    val tempLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val showSubsDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val event: (SubscriptionUiEvent) -> Unit = {},
    val isMonthlySelected: StateFlow<Boolean> = MutableStateFlow(true)
)

//Data
data class SubscriptionUiState(
    val activePlan: Int = 1,
    val listOfBenefits: ArrayList<String> = arrayListOf()
)

//Events
sealed interface SubscriptionUiEvent {
    data class OnClickOfPlan(val clickedPlan: Int) : SubscriptionUiEvent
    data class ShowLogoutSheet(val show: Boolean) : SubscriptionUiEvent
    data object OnClickOfGotIt : SubscriptionUiEvent
    data object DoSubscribe : SubscriptionUiEvent
    data object DoLogOut : SubscriptionUiEvent
    data object StartFreeTrial : SubscriptionUiEvent
    data class PerformSubscriptionTypeClick(val isMonthlySelected: Boolean) : SubscriptionUiEvent
}