package com.medrevpatient.mobile.app.data.source.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.medrevpatient.mobile.app.data.source.remote.EndPoints.ResultType.FOR_GENERAL

@Entity(tableName = "DemandClasses")
data class DemandClassesEntity(
    @PrimaryKey
    val id: String = "",
    val videoUrl: String = "",
    val thumbnail: String = "",
    val duration: String = "",
    val level: Int = 0,
    val type: Int = FOR_GENERAL.value,
    val videoTitle: String = "",
)