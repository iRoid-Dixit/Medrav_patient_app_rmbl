package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class ForMe(
    @SerializedName("programs")
    val programs: List<Program> = emptyList(),
    @SerializedName("notes")
    val notes: List<Note> = emptyList(),
    @SerializedName("recipes")
    val recipes: List<Recipe> = listOf()
)