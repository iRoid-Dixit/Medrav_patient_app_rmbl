package com.medrevpatient.mobile.app.domain.usecases

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.map
import com.medrevpatient.mobile.app.data.source.local.room.DemandClassesDao
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.Mapper.toOnDemandClasses
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DemandClassesUseCase @Inject constructor(
    private val repository: ApiRepository,
    private val dao: DemandClassesDao,
    private val networkMonitor: NetworkMonitor,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        type: Int,
    ): Flow<PagingData<OnDemandClasses>> {
        return networkMonitor.isOnline.flatMapLatest { isOnline ->
            if (isOnline) {
                dao.getByType(type).flatMapLatest { dbList ->
                    repository.getOnDemandClasses(type)
                        .map { pagingData ->
                            pagingData.map { item ->
                                dbList.find { it.id == item.id }?.let { dbItem ->
                                    item.copy(
                                        videoUrl = dbItem.videoUrl,
                                        thumbnail = dbItem.thumbnail
                                    )
                                } ?: item
                            }
                        }
                }.flowOn(Dispatchers.IO)
            } else {
                dao.getByType(type)
                    .map { localList ->
                        PagingData.from(
                            localList.toOnDemandClasses(), sourceLoadStates =
                            LoadStates(
                                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                                append = LoadState.NotLoading(endOfPaginationReached = true)
                            )
                        )
                    }
            }.flowOn(Dispatchers.IO)
        }.flowOn(Dispatchers.IO)
    }
}