package com.medrevpatient.mobile.app.ux.main.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.navigation.DefaultNavBarConfig
import com.medrevpatient.mobile.app.navigation.ViewModelNavBar
import com.medrevpatient.mobile.app.navigation.ViewModelNavBarImpl
import com.medrevpatient.mobile.app.ux.main.bottombar.NavBarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSettingUiStateUseCase: GetProfileUiStateUseCase
) : ViewModel(), ViewModelNavBar<NavBarItem> by ViewModelNavBarImpl(
    NavBarItem.PROFILE, DefaultNavBarConfig(
        NavBarItem.getNavBarItemRouteMap()
    )
) {
    val uiState: ProfileUiState = getSettingUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}