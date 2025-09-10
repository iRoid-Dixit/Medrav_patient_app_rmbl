package com.medrevpatient.mobile.app.model.domain.response.sideEffect

import com.google.gson.annotations.SerializedName



data class SideEffectQuestion(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("question")
    val question: String = "",
    
    @SerializedName("order")
    val order: Int = 0,
    
    @SerializedName("options")
    val options: List<SideEffectOption> = emptyList()
)

data class SideEffectOption(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("option")
    val option: String = ""
)



