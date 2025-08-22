package com.griotlegacy.mobile.app.model.domain.request.mainReq

import com.google.gson.annotations.SerializedName

class ChangePasswordReq(
    @SerializedName("oldPassword") val oldPassword: String? = null,
    @SerializedName("newPassword") val newPassword: String? = null,
    @SerializedName("confirmPassword") val confirmPassword: String? = null,
)
