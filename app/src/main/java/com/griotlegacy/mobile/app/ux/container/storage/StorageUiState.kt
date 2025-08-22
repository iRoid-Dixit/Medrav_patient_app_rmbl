package com.griotlegacy.mobile.app.ux.container.storage

import android.content.Context
import com.griotlegacy.mobile.app.model.domain.response.container.storege.StorageResponse
import com.revenuecat.purchases.models.StoreProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StorageUiState(
    //data
    val storageDataFlow: StateFlow<StorageDataState?> = MutableStateFlow(null),
    val userStorageData: StateFlow<StorageResponse?> = MutableStateFlow(null),
    //event
    val event: (StorageUiEvent) -> Unit = {}
)

data class StorageDataState(
    val showLoader: Boolean = false,
    val selectedProduct: StoreProduct? = null
)

sealed interface StorageUiEvent {
    data class GetContext(val context: Context) : StorageUiEvent
    data class SubscriptionStatus(val subscriptionStatus: Boolean) : StorageUiEvent
    data object BackClick : StorageUiEvent
    data object SubscriptionClick : StorageUiEvent

}