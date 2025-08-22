package com.griotlegacy.mobile.app.model.domain.response.subscription


import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("is_subscribed") val isSubscribed: Boolean,
    @SerializedName("subscription") val subscription: SubscriptionData?,
    @SerializedName("app_url") val appUrl: String
)

data class SubscriptionData(
    @SerializedName("ios_product_id") val iosProductId: Any,
    @SerializedName("android_product_id") val androidProductId: String,
    @SerializedName("subscription_type") val subscriptionType: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("expiry_date") val expiryDate: String
)