package com.medrevpatient.mobile.app.domain

import com.medrevpatient.mobile.app.data.source.local.room.DemandClassesEntity
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses

object Mapper {

    fun DemandClassesEntity.toOnDemandClasses(): OnDemandClasses {
        return OnDemandClasses(
            id = id,
            videoUrl = videoUrl,
            thumbnail = thumbnail,
            duration = duration,
            level = level,
            videoTitle = videoTitle,
        )
    }

    fun OnDemandClasses.toDemandClassesEntity(): DemandClassesEntity {
        return DemandClassesEntity(
            id = id,
            videoUrl = videoUrl,
            thumbnail = thumbnail,
            duration = duration,
            level = level,
            type = type,
            videoTitle = videoTitle,
        )
    }

    fun List<DemandClassesEntity>.toOnDemandClasses(): List<OnDemandClasses> {
        return map { it.toOnDemandClasses() }
    }

}