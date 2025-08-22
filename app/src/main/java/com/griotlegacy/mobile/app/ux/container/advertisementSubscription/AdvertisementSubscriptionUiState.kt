package com.griotlegacy.mobile.app.ux.container.advertisementSubscription


import android.content.Context
import com.revenuecat.purchases.models.StoreProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AdvertisementUiState(
    //data
    val storageDataFlow: StateFlow<StorageDataState?> = MutableStateFlow(null),

    //event
    val event: (StorageUiEvent) -> Unit = {}
)

data class StorageDataState(
    val showLoader: Boolean = false,
)

sealed interface StorageUiEvent {
    data class GetContext(val context: Context) : StorageUiEvent
    data object BackClick : StorageUiEvent
    data class DoSubscribe(val activityContext: Context, val product: StoreProduct) : StorageUiEvent

}