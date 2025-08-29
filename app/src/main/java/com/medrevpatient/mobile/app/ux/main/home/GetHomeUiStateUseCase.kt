package com.medrevpatient.mobile.app.ux.main.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.report.ReportUserPostReq
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.imageDisplay.ImageDisplayActivity
import com.medrevpatient.mobile.app.ux.main.griotLegacy.GriotLegacyRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetHomeUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
    ) {
    private val homeUiDataFlow = MutableStateFlow(HomeUiDataState())
    private lateinit var context: Context
    operator fun invoke(
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): HomeUiState {
        return HomeUiState(
            homeUiDataFlow = homeUiDataFlow,
            event = { homeUiEvent ->
                homeUiEvent(
                    coroutineScope = coroutineScope,
                    event = homeUiEvent,
                    navigate = navigate,

                )
            }
        )
    }
    private fun homeUiEvent(
        coroutineScope: CoroutineScope,
        event: HomeUiEvent,
        navigate: (NavigationAction) -> Unit,
    ) {
        when (event) {
            is HomeUiEvent.GetContext -> {
                this.context = event.context
            }
        }
    }
}