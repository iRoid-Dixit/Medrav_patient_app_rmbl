package com.medrevpatient.mobile.app.domain.usecases

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.medrevpatient.mobile.app.data.source.local.room.DemandClassesDao
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_CHILD_DIR
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_CLASS_JSON
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_FILE_NAME
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_FILE_TYPE
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_FILE_URL
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_PARENT_DIR
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import com.medrevpatient.mobile.app.utils.ext.toJsonString
import com.medrevpatient.mobile.app.worker.FileDownloadWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class OfflineCachingUseCase @Inject constructor(
    private val demandClassesDao: DemandClassesDao,
    private val workManager: WorkManager
) {

    operator fun invoke(
        onDemandClasses: OnDemandClasses,
        scope: CoroutineScope,
    ) {

        scope.launch(Dispatchers.IO) {

            val ids = demandClassesDao.getIds() ?: emptyList()
            Timber.d("$ids")
            ids.contains(onDemandClasses.id).ifTrue { return@launch }

            val videoData = workDataOf(
                KEY_CLASS_JSON to onDemandClasses.toJsonString(),
                KEY_FILE_NAME to onDemandClasses.id,
                KEY_FILE_TYPE to onDemandClasses.videoUrl.substringAfterLast('.'),
                KEY_FILE_URL to onDemandClasses.videoUrl,
                KEY_PARENT_DIR to "classes",
                KEY_CHILD_DIR to "videos",
            )

            val downloadConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .build()

            val videoWorkRequest = OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(videoData)
                .setConstraints(downloadConstraints)
                .build()

            val thumbnailData = workDataOf(
                KEY_CLASS_JSON to onDemandClasses.toJsonString(),
                KEY_FILE_NAME to "${onDemandClasses.id}_thumb",
                KEY_FILE_TYPE to onDemandClasses.thumbnail.substringAfterLast('.'),
                KEY_FILE_URL to onDemandClasses.thumbnail,
                KEY_PARENT_DIR to "classes",
                KEY_CHILD_DIR to "thumbnails",
            )

            val thumbnailWorkRequest = OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(thumbnailData)
                .setConstraints(downloadConstraints)
                .build()

            workManager.beginUniqueWork(
                onDemandClasses.id,
                ExistingWorkPolicy.KEEP,
                thumbnailWorkRequest
            ).then(videoWorkRequest)
                .enqueue()
        }
    }

}