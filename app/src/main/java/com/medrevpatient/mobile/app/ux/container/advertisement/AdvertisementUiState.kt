package com.medrevpatient.mobile.app.ux.container.advertisement

import android.content.Context
import androidx.paging.PagingData
import com.medrevpatient.mobile.app.model.domain.response.advertisement.AdvertisementResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AdvertisementUiState(
    //data
    val advertisementDataFlow: StateFlow<AdvertisementDataState?> = MutableStateFlow(null),

    val advertisementList: StateFlow<PagingData<AdvertisementResponse>> = MutableStateFlow(
        PagingData.empty()
    ),
    //event
    val event: (AdvertisementUiEvent) -> Unit = {}
)

data class AdvertisementDataState(
    val showLoader: Boolean = false,


    )

sealed interface AdvertisementUiEvent {
    data class GetContext(val context: Context) : AdvertisementUiEvent
    data object BackClick : AdvertisementUiEvent
    data object NavigateAddAdvertisement : AdvertisementUiEvent
    data object AdvertisementAPICall : AdvertisementUiEvent
    data class EditAdvertisement(val advertisement: AdvertisementResponse) : AdvertisementUiEvent

}