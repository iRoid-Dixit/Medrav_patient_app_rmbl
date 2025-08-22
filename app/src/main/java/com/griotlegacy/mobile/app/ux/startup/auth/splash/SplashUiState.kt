package com.griotlegacy.mobile.app.ux.startup.auth.splash

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SplashUiState(
    val splashUiDataFlow: StateFlow<SplashUiData?> = MutableStateFlow(null),
    val event: (SplashUiEvent) -> Unit = {}
)
//data class SplashUiData(
//
//)
data class SplashUiData(
    val showAppUpdateDialog: Boolean = false,
    val isForceUpdate: Boolean = false,
    val appLink: String = "",
    val appMessage: String = ""
)
sealed interface SplashUiEvent {
    data class GetContext(val context: Context) : SplashUiEvent
    data class RestartAppKey(val key: String) : SplashUiEvent
    data class GetIntentData(val bundle: Bundle) : SplashUiEvent
    data object OnClickOfCancel : SplashUiEvent
    data class OnClickOfUpdate(val appContext: Context) : SplashUiEvent


}