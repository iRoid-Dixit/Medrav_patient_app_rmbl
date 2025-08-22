package com.medrevpatient.mobile.app.ux.container.storageSubscription

import android.content.Context
import android.util.Log
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.SubscriptionInfoReq
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.utils.ext.requireActivity
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.models.GoogleReplacementMode
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.models.googleProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetStorageSubscriptionUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val storageDataFlow = MutableStateFlow(StorageDataState())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): StorageUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return StorageUiState(
            storageDataFlow = storageDataFlow,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }
    private fun contactUsUiEvent(
        event: StorageUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope

    ) {
        when (event) {
            is StorageUiEvent.GetContext -> {
                this.context = event.context
            }
            StorageUiEvent.BackClick -> {
                navigate(NavigationAction.Pop())
            }
            is StorageUiEvent.DoSubscribe -> {
                purchaseProduct(
                    coroutineScope = coroutineScope,
                    item = event.product,
                    context = event.activityContext,
                    navigate = navigate
                )
            }
        }
    }
    private fun purchaseProduct(
        coroutineScope: CoroutineScope,
        item: StoreProduct,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        Logger.d("Purchase product: $item")
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                val currentSubscription = customerInfo.activeSubscriptions.firstOrNull()
                val purchaseParams = PurchaseParams.Builder(context.requireActivity(), item)
                currentSubscription?.let { oldProductId ->
                    purchaseParams.oldProductId(oldProductId)
                    // Only set replacement mode if it's a downgrade AND supported
                    if (isDowngradeScenario(oldProductId, item.googleProduct?.productId ?: "")) {
                        try {
                            purchaseParams.googleReplacementMode(GoogleReplacementMode.DEFERRED)
                        } catch (_: Exception) {
                            // Fallback to immediate if deferred isn't supported
                            purchaseParams.googleReplacementMode(GoogleReplacementMode.WITHOUT_PRORATION)
                            Logger.e("Replacement mode not supported, falling back to WITHOUT_PRORATION")
                        }
                    }
                }
                Purchases.sharedInstance.purchase(
                    purchaseParams.build(),
                    object : PurchaseCallback {
                        override fun onCompleted(
                            storeTransaction: StoreTransaction,
                            customerInfo: CustomerInfo
                        ) {
                            Logger.e("Updated Customer Info: ${Gson().toJson(customerInfo)}")
                            val activeSubs = customerInfo.activeSubscriptions
                            val entitlements = customerInfo.entitlements.active
                            Logger.e("Active Subscriptions: $activeSubs")
                            Logger.e("Active Entitlements: $entitlements")
                            // Send subscription details to your backend
                            Logger.e("Purchase Token: ${storeTransaction.purchaseToken}")
                            Logger.e("Original Transaction ID: ${storeTransaction.purchaseToken}")
                            storePurchaseInfo(
                                context = this@GetStorageSubscriptionUiStateUseCase.context,
                                coroutineScope = coroutineScope,
                                subscriptionInfoReq = SubscriptionInfoReq(
                                    productId = item.googleProduct?.productId ?: "",
                                    token = storeTransaction.purchaseToken,
                                    platform = 1,
                                    isTestEnvironment = true
                                ),
                                navigate = navigate
                            )
                        }
                        override fun onError(
                            error: com.revenuecat.purchases.PurchasesError,
                            userCancelled: Boolean
                        ) {
                            if (!userCancelled) {
                                Logger.e("Error completing purchase: ${error.message}")
                                Log.d("TAG", "purchaseProduct: ${error.message}")
                            }
                        }
                    }
                )
            }
            override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                Logger.e("Error getting customer info: ${error.message}")
            }
        })
    }
    private fun isDowngradeScenario(currentProductId: String, newPlanId: String): Boolean {
        return when {
            currentProductId.contains("m_a_10_gb") -> false
            currentProductId.contains("h_25_gb") && newPlanId.contains("m_a_10_gb") -> true
            currentProductId.contains("l_a_100_gb") && (newPlanId.contains("h_25_gb") || newPlanId.contains("m_a_10_gb")) -> true
            currentProductId.contains("a_h_200_gb") && (newPlanId.contains("l_a_100_gb") || newPlanId.contains("h_25_gb") || newPlanId.contains("m_a_10_gb")) -> true
            currentProductId.contains("l_v_k_500_gb") && (newPlanId.contains("a_h_200_gb") || newPlanId.contains("l_a_100_gb") || newPlanId.contains("h_25_gb") || newPlanId.contains("m_a_10_gb")) -> true
            currentProductId.contains("t_c_1_tb") -> true
            else -> false
        }
    }
    private fun storePurchaseInfo(
        context: Context,
        coroutineScope: CoroutineScope,
        subscriptionInfoReq: SubscriptionInfoReq,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.storePurchaseInfo(subscriptionInfoReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                    }
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        navigate(
                            NavigationAction.PopWithResult(
                                resultValues = listOf(
                                    PopResultKeyValue("subscriptionStatus", true),
                                )
                            )
                        )
                        showOrHideLoader(false)
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }
    private fun showOrHideLoader(showLoader: Boolean) {
        storageDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
}


