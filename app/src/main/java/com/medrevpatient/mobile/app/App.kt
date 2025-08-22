package com.medrevpatient.mobile.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.ux.startup.StartupActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.GetStoreProductsCallback
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.googleProduct
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {

    private val appPreferenceDataStore: AppPreferenceDataStore? = null
    private var accessToken: String = ""

    companion object {
        var instance: App? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidThreeTen.init(this) // <- REQUIRED
        getAccessToken()
        initRevenueCat()
        Log.d("TAG", "getAccessToken: ${getAccessToken()}")
    }

    private fun initRevenueCat() {
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                this,
                Constants.Subscription.PUBLIC_API_KEY
            ).build()
        )

        /*
        Whenever the `sharedInstance` of Purchases updates the CustomerInfo cache, this method will be called.

        Note: CustomerInfo is not pushed to each Purchases client, it has to be fetched.
        This list is only called when the SDK updates its cache after an app launch, purchase, restore, or fetch.
        You still need to call `Purchases.shared.customerInfo` to fetch CustomerInfo regularly.
         */
        Purchases.sharedInstance.updatedCustomerInfoListener =
            UpdatedCustomerInfoListener { customerInfo ->
                // - Update our user's customerInfo object
                Logger.d("Customer info updated: $customerInfo")

                // Check for active subscriptions
                customerInfo.activeSubscriptions.forEach { subscription ->
                    Logger.d("Active subscription: $subscription")
                }

                // Check entitlements
                customerInfo.entitlements.all.forEach { (key, entitlement) ->
                    if (entitlement.isActive) {
                        Logger.d("Active entitlement: $key")
                    }
                }
            }

        // Pre-fetch products for better performance
        fetchProducts()
    }

    private fun fetchProducts() {
        // List of product IDs for storage plans (these should match the IDs in your RevenueCat dashboard)
        val storageProductIds = Constants.Subscription.SubscriptionPlan.entries.map { it.productId }

        // List of product IDs for advertisement click plans
        val advertisementProductIds = Constants.Subscription.AdvertisementClickPlan.entries.map { it.productId }

        // Fetch storage products
        Purchases.sharedInstance.getProducts(storageProductIds, object : GetStoreProductsCallback {
            override fun onReceived(products: List<StoreProduct>) {
                // Successfully retrieved storage products
                Logger.d("Fetched ${products.size} storage products from RevenueCat")
                products.forEach { product ->
                    Logger.d("Storage Product: ${product.title}, Price: ${product.price}, ID: ${product.googleProduct?.productId}")
                }
            }

            override fun onError(error: PurchasesError) {
                // Handle the error
                Logger.e("Error fetching storage products: ${error.message}")
            }
        })

        // Fetch advertisement products
        Purchases.sharedInstance.getProducts(advertisementProductIds, object : GetStoreProductsCallback {
            override fun onReceived(products: List<StoreProduct>) {
                // Successfully retrieved advertisement products
                Logger.d("Fetched ${products.size} advertisement products from RevenueCat")
                products.forEach { product ->
                    Logger.d("Advertisement Product: ${product.title}, Price: ${product.price}, ID: ${product.googleProduct?.productId}")
                }
            }

            override fun onError(error: PurchasesError) {
                // Handle the error
                Logger.e("Error fetching advertisement products: ${error.message}")
            }
        })
    }

    fun getAccessToken(): String {
        try {
            runBlocking {
                val token = appPreferenceDataStore?.getUserAuthData()?.accessToken ?: ""
                accessToken = token
            }
        } catch (e: Exception) {
            Logger.e("Exception getting access token: ${e.message}")
        }
        return accessToken
    }

    fun restartApp() {
        val intent = Intent(this, StartupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.BundleKey.RESET, Constants.BundleKey.RESTART_APP)
        startActivity(intent)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        // Implementation not needed for this use case
    }

    override fun onActivityStarted(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivityResumed(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivityPaused(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivityStopped(p0: Activity) {
        // Implementation not needed for this use case
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        // Implementation not needed for this use case
    }

    override fun onActivityDestroyed(p0: Activity) {
        // Implementation not needed for this use case
    }
}
