package com.griotlegacy.mobile.app.ux.container.advertisementSubscription

import android.content.Context
import android.util.Log
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.model.domain.request.SubscriptionInfoReq
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.utils.ext.requireActivity
import com.griotlegacy.mobile.app.ux.container.advertisement.AdvertisementRoute
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
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

class GetAdvertisementSubscriptionUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val storageDataFlow = MutableStateFlow(StorageDataState())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        advertisementId: String,
        navigate: (NavigationAction) -> Unit,
    ): AdvertisementUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return AdvertisementUiState(
            storageDataFlow = storageDataFlow,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    advertisementId = advertisementId
                )
            }
        )
    }
    private fun contactUsUiEvent(
        event: StorageUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        advertisementId: String,

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
                    navigate = navigate,
                    advertisementId = advertisementId
                )
            }
        }
    }
    private fun purchaseProduct(
        coroutineScope: CoroutineScope,
        item: StoreProduct,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        advertisementId: String,
    ) {
        Logger.d("Purchase product: $item")
        Logger.d("Product ID: ${item.id}")
        Logger.d("Product title: ${item.title}")
        Logger.d("Product price: ${item.price.formatted}")

        // Note: To remove quantity selector from Google Play popup,
        // configure products as "Non-Consumable" in RevenueCat dashboard
        // instead of "Consumable"
        
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onReceived(customerInfo: CustomerInfo) {
                val purchaseParams = PurchaseParams.Builder(context.requireActivity(), item)

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
                                context = this@GetAdvertisementSubscriptionUiStateUseCase.context,
                                coroutineScope = coroutineScope,
                                subscriptionInfoReq = SubscriptionInfoReq(
                                    advertisementId = advertisementId,
                                    productId = item.googleProduct?.productId ?: "",
                                    token = storeTransaction.purchaseToken,
                                    platform = Constants.Subscription.ANDROID,
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


    private fun storePurchaseInfo(
        context: Context,
        coroutineScope: CoroutineScope,
        subscriptionInfoReq: SubscriptionInfoReq,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.storeAdvertisementInfo(subscriptionInfoReq).collect {
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
                        showSuccessMessage(
                            context = context,
                            it.data?.message ?: "Something went wrong!"
                        )
                        navigate(NavigationAction.PopAndNavigate(AdvertisementRoute.createRoute()))
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


