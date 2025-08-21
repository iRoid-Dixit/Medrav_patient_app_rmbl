package com.medrevpatient.mobile.app.ux.startup

import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.ViewModelNav
import com.medrevpatient.mobile.app.navigation.ViewModelNavImpl
import com.medrevpatient.mobile.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

//App Starting
@HiltViewModel
class StartupViewModel @Inject constructor(private val localManager: LocalManager) : BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    var isReady: Boolean = false
        private set

    var startDestination = ""

    init {
        startup()
    }

    //**** Enable this function is you want to any operation on app start up... ****\\
    fun startup() = viewModelScope.launch {
        // run any startup/initialization code here (NOTE: these tasks should NOT exceed 1000ms (per Google Guidelines))
        startDestination = RouteMaker.SplashRoute.routeDefinition.value
        // Startup finished
        isReady = true
    }
}