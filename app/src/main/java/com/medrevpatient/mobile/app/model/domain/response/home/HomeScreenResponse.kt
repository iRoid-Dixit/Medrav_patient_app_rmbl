package com.medrevpatient.mobile.app.model.domain.response.home


import com.google.gson.annotations.SerializedName

data class HomeScreenResponse(
    @SerializedName("today_script") val todayScript: ScriptData,
    @SerializedName("past_script") val pastScript: List<ScriptData>
)

data class ScriptData(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("theme") val theme: String,
    @SerializedName("verse") val verse: String,
    @SerializedName("audio") val audio: String,
    @SerializedName("is_played") val isPlayed: Boolean
)