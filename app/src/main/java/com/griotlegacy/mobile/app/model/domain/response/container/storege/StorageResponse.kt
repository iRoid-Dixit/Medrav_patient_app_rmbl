package com.griotlegacy.mobile.app.model.domain.response.container.storege

import com.google.gson.annotations.SerializedName

data class StorageResponse(
    @SerializedName("percentageUsed") val percentageUsed: Double? = null,
    @SerializedName("photoProgress") val photoProgress: Double? = null,
    @SerializedName("storageLimit") val storageLimit: Double? = null,
    @SerializedName("totalPhotoKB") val totalPhotoKB: Double? = null,
    @SerializedName("totalStorageUsedGB") val totalStorageUsedGB: Double? = null,
    @SerializedName("totalVideoKB") val totalVideoKB: Double? = null,
    @SerializedName("videoProgress") val videoProgress: Double? = null
)
