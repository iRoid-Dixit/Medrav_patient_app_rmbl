package com.griotlegacy.mobile.app.ux.startup
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.griotlegacy.mobile.app.model.base.BaseViewModel
import com.griotlegacy.mobile.app.navigation.ViewModelNav
import com.griotlegacy.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel
@Inject constructor(
): BaseViewModel(), ViewModelNav by ViewModelNavImpl() {
    private var isReady: Boolean = false

    var startDestination = ""

     //**** Enable this function is you want to any operation on app start up... ****\\
    fun startup() = viewModelScope.launch {
        // run any startup/initialization code here (NOTE: these tasks should NOT exceed 1000ms (per Google Guidelines))
        Logger.i { "Startup task..." }
         // startDestination = appPreferenceDataStore.getStatUpStartDestination() ?: Constants.AppScreen.START_UP
        // Startup finished
        isReady = true
        Logger.i { "Startup finished" }
    }


}