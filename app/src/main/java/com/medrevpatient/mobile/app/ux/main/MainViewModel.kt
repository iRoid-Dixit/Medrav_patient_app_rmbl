package com.medrevpatient.mobile.app.ux.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.request.RegisterForPushRequest
import com.medrevpatient.mobile.app.navigation.DefaultNavBarConfig
import com.medrevpatient.mobile.app.navigation.NavBarItem
import com.medrevpatient.mobile.app.navigation.ViewModelNavBar
import com.medrevpatient.mobile.app.navigation.ViewModelNavBarImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(private val apiRepository: ApiRepository) : ViewModel(), ViewModelNavBar<NavBarItem> by ViewModelNavBarImpl(
    NavBarItem.PROGRAM_WORKOUT, DefaultNavBarConfig(
        NavBarItem.getNavBarItemRouteMap()
    )
) {
    fun registerForPushAPI(token: String, context: Context) {
        val req = RegisterForPushRequest(
            token = token,
            deviceId = AppUtils.getDeviceId(context),
            platform = Constants.ANDROID
        )
        Timber.d("TAG", "registerForPushAPI: ${AppUtils.getDeviceId(context)}")
        viewModelScope.launch {
            apiRepository.registerPush(req).collect {
                when (it) {
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {}
                }
            }
        }
    }
}