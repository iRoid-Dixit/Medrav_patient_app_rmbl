package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.domain.response.Meta

data class Recipes(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val data: List<Data> = listOf(),
    @SerializedName("meta")
    val meta: Meta = Meta(),
    @SerializedName("filterTags")
    val filterTags: List<String> = listOf()
) {
    data class Data(
        @SerializedName("_id")
        val id: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("image")
        val image: String = ""
    )
}