package com.medrevpatient.mobile.app.ux.main
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.base.BaseViewModel
import com.medrevpatient.mobile.app.model.domain.request.TokenStoreReq
import com.medrevpatient.mobile.app.navigation.DefaultNavBarConfig
import com.medrevpatient.mobile.app.navigation.ViewModelNavBar
import com.medrevpatient.mobile.app.navigation.ViewModelNavBarImpl
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.ux.main.bottombar.NavBarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,

    ) : BaseViewModel(), ViewModelNavBar<NavBarItem> by ViewModelNavBarImpl(
    NavBarItem.HOME, DefaultNavBarConfig(
        NavBarItem.getNavBarItemRouteMap()
    )
) {
    fun registerForPushAPI(token: String, context: Context) {
        val req = TokenStoreReq(
            type = Constants.DEVICE_TYPE,
            deviceId = AppUtils.getDeviceId(context),
            token = token
        )
        Log.d("TAG", "registerForPushAPI: ${AppUtils.getDeviceId(context)}")
        viewModelScope.launch {
            apiRepository.storeFCMToken(req).collect {
                when (it) {
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {}
                    is NetworkResult.UnAuthenticated -> {}
                }
            }
        }
    }






}