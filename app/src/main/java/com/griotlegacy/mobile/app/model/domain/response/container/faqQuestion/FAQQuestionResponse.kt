package com.griotlegacy.mobile.app.model.domain.response.container.faqQuestion

import com.google.gson.annotations.SerializedName

data class FAQQuestionResponse(

    @SerializedName("_id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("answer") val answer: String? = null,
    @SerializedName("question") val question: String? = null

)
