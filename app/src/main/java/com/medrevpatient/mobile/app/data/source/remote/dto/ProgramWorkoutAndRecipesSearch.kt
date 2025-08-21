package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class ProgramWorkoutAndRecipesSearch(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("Subtitle")
    val subtitle: String? = null,
    @SerializedName("Type")
    val type: Int = 0
) {
    val isProgramType get() = type == 1
}