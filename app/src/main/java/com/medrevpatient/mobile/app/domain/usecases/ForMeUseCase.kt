package com.medrevpatient.mobile.app.domain.usecases

import com.medrevpatient.mobile.app.data.source.local.room.DemandClassesDao
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import javax.inject.Inject

//TODO: Remove this file
class ForMeUseCase @Inject constructor(
    private val repository: ApiRepository,
    private val dao: DemandClassesDao,
    private val networkMonitor: NetworkMonitor,
) {
/*
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(type: Int): Flow<NetworkResult<ApiResponse<ForMe>>> {
        return networkMonitor.isOnline.flatMapLatest { isOnline ->
            if (isOnline) {
                dao.getByType(type).flatMapLatest { dbList ->
                    repository.getForMe()
                        .map { networkResult ->
                            when (networkResult) {
                                is NetworkResult.Success -> {
                                    val updatedMoveNow =
                                        networkResult.data?.data?.moveNow?.map { item ->
                                            dbList.find { it.id == item.id }?.let { dbItem ->
                                                Timber.d("ForMeUseCase" + "Merging data: ${item.id}, VideoUrl: ${dbItem.videoUrl}, Thumbnail: ${dbItem.thumbnail}")

                                                item.copy(
                                                    videoUrl = dbItem.videoUrl,
                                                    thumbnail = dbItem.thumbnail
                                                )
                                            } ?: item
                                        }
                                    val updatedData = ApiResponse(
                                        data = networkResult.data?.data?.copy(
                                            moveNow = updatedMoveNow ?: emptyList()
                                        ),
                                        status = networkResult.data?.status ?: 0,
                                        message = networkResult.data?.message ?: ""
                                    )
                                    NetworkResult.Success(updatedData)
                                }

                                is NetworkResult.Error -> networkResult
                                is NetworkResult.Loading -> networkResult
                            }
                        }
                }.flowOn(Dispatchers.IO)
            } else {
                dao.getByType(type)
                    .map { dbList ->
                        val forMe =
                            ForMe(moveNow = dbList.toOnDemandClasses())  // Adjust according to your data class mapping
                        NetworkResult.Success(ApiResponse(forMe))
                    }
                    .flowOn(Dispatchers.IO)
            }
        }
    }*/
}