package com.griotlegacy.mobile.app.model.domain.response.message

import com.google.gson.annotations.SerializedName

data class MessageResponse (
    @SerializedName("message") var message:String=""

)