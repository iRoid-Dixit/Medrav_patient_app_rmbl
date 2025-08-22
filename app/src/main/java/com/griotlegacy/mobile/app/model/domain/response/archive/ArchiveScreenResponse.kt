package com.griotlegacy.mobile.app.model.domain.response.archive


import com.google.gson.annotations.SerializedName
import com.griotlegacy.mobile.app.model.domain.response.home.ScriptData

data class ArchiveScreenResponse(
    @SerializedName("today_script") val todayScript: ScriptData,
    @SerializedName("past_script") val pastScriptList: List<ScriptData>
)