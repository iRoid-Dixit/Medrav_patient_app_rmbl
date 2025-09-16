package com.medrevpatient.mobile.app.model.domain.request.sideEffect

import com.google.gson.annotations.SerializedName

data class SideEffectAnswerRequest(
    @SerializedName("answers")
    val answers: List<SideEffectAnswer>
)

data class SideEffectAnswer(
    @SerializedName("questionId")
    val questionId: Int,
    
    @SerializedName("optionId")
    val optionId: Int
)
