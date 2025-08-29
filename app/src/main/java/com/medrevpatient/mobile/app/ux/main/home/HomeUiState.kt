package com.medrevpatient.mobile.app.ux.main.home

import android.content.Context
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    //data
    val homeUiDataFlow: StateFlow<HomeUiDataState?> = MutableStateFlow(null),
    val event: (HomeUiEvent) -> Unit = {}
)
data class HomeUiDataState(
    val showLoader: Boolean = false,

)
sealed interface HomeUiEvent {

    data class GetContext(val context: Context) : HomeUiEvent

}