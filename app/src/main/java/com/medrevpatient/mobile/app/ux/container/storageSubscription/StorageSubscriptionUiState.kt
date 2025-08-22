package com.medrevpatient.mobile.app.ux.container.storageSubscription


import android.content.Context
import com.revenuecat.purchases.models.StoreProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StorageUiState(
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