package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("difficultyLevel")
    val difficultyLevel: Int = 0,
    @SerializedName("isPin")
    val isPin: Boolean = false,
    @SerializedName("image")
    val image: String = "",
    @SerializedName("nutritionalCategory")
    val nutritionalCategory: List<String> = emptyList(),
    @SerializedName("ingredients")
    val ingredients: List<Ingredient> = emptyList()
) {

    data class Ingredient(
        @SerializedName("_id")
        val id: String = "",
        @SerializedName("item")
        val item: String = "",
        @SerializedName("quantity")
        val quantity: String = "",
        @SerializedName("unit")
        val unit: String = ""
    )

    val difficultyLevelLabel: String
        get() = when (difficultyLevel) {
            1 -> "Very Easy"
            2 -> "Easy"
            3 -> "Medium"
            4 -> "Hard"
            else -> "Unknown"
        }

}