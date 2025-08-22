package com.medrevpatient.mobile.app.ux.container.about

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AboutUiState(
    //data
    val aboutUsDataFlow: StateFlow<AboutUSDataState?> = MutableStateFlow(null),
    //event
    val event: (AboutUiEvent) -> Unit = {}
)

data class AboutUSDataState(
    val screen: String = "",
    val showLoader: Boolean = false,
    val termsUrl: String = "",
)

sealed interface AboutUiEvent {

    data object BackClick : AboutUiEvent

}