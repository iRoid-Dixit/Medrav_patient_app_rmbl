package com.medrevpatient.mobile.app.worker

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.medrevpatient.mobile.app.data.source.local.room.DemandClassesDao
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.domain.Mapper.toDemandClassesEntity
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_CHILD_DIR
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_CLASS_JSON
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_FILE_NAME
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_FILE_TYPE
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_FILE_URL
import com.medrevpatient.mobile.app.utils.Constants.FileParams.KEY_PARENT_DIR
import com.medrevpatient.mobile.app.utils.ext.fromJsonString
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

@HiltWorker
class FileDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val demandClassesDao: DemandClassesDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val dataJSON = inputData.getString(KEY_CLASS_JSON) ?: return Result.failure()
            val fileName = inputData.getString(KEY_FILE_NAME) ?: return Result.failure()
            val fileType = inputData.getString(KEY_FILE_TYPE) ?: return Result.failure()
            val fileUrl = inputData.getString(KEY_FILE_URL) ?: return Result.failure()
            val parentDir = inputData.getString(KEY_PARENT_DIR) ?: return Result.failure()
            val childDir = inputData.getString(KEY_CHILD_DIR) ?: return Result.failure()

            val uri = downloadFile(
                fileName = fileName,
                fileType = fileType,
                fileUrl = fileUrl,
                parentDir = parentDir,
                subDirName = childDir,
                context = context,
                onFailure = {
                    Result.retry()
                    Timber.d("File download failed: $it")
                },
                sizeLimitReached = {
                    clearCache(demandClassesDao, context, parentDir)
                }
            )

            uri?.let {
                when (fileType.lowercase()) {
                    "mp4" -> {
                        updateVideoUriInDB(
                            demandClassesDao = demandClassesDao,
                            dataJSON = dataJSON,
                            uri = uri,
                            updateType = DataUpdateType.VIDEO
                        )
                    }

                    "jpeg", "png", "jpg" -> {
                        updateVideoUriInDB(
                            demandClassesDao = demandClassesDao,
                            dataJSON = dataJSON,
                            uri = uri,
                            updateType = DataUpdateType.IMAGE
                        )
                    }

                    else -> {}
                }
            } ?: Result.retry()

            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun clearCache(
        demandClassesDao: DemandClassesDao,
        context: Context,
        parentDir: String? = null
    ): Result {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                demandClassesDao.clearAll()
            }.invokeOnCompletion {
                val cacheDir = context.getExternalFilesDir(parentDir) ?: return@invokeOnCompletion
                cacheDir.deleteRecursively()
            }
            Result.failure()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }

    }


    private fun updateVideoUriInDB(
        demandClassesDao: DemandClassesDao,
        dataJSON: String,
        uri: Uri,
        updateType: DataUpdateType
    ): Result {

        //Get Original Data
        dataJSON.fromJsonString<OnDemandClasses>()?.let { onDemandClasses ->

            CoroutineScope(Dispatchers.IO).launch {

                //Get Offline Data
                val modifiedClasses =
                    when (updateType) {
                        DataUpdateType.IMAGE -> {
                            demandClassesDao.getById(onDemandClasses.id)
                                ?.copy(
                                    thumbnail = uri.toString(),
                                    type = onDemandClasses.type
                                )
                                ?: onDemandClasses.copy(
                                    thumbnail = uri.toString()
                                ).toDemandClassesEntity()
                        }

                        DataUpdateType.VIDEO -> {
                            demandClassesDao.getById(onDemandClasses.id)
                                ?.copy(videoUrl = uri.toString(), type = onDemandClasses.type)
                                ?: onDemandClasses.copy(
                                    videoUrl = uri.toString()
                                ).toDemandClassesEntity()

                        }
                    }

                demandClassesDao.update(modifiedClasses)
            }

        } ?: Result.failure()

        return Result.success()
    }
}


enum class DataUpdateType {
    IMAGE, VIDEO
}


fun downloadFile(
    fileName: String,
    fileType: String,
    fileUrl: String,
    parentDir: String? = null,
    subDirName: String,
    context: Context,
    onFailure: (String) -> Unit,
    sizeLimitReached: () -> Unit
): Uri? {

    //File Definition
    val cacheDir = context.getExternalFilesDir(parentDir) ?: return null
    val subDir = File(cacheDir, subDirName)
    if (!subDir.exists()) {
        subDir.mkdirs() // Ensure the directory is created
    }


    // Create the file inside the subdirectory
    val file = File(subDir, "$fileName.$fileType")

    CoroutineScope(Dispatchers.IO).launch {
        val fileSize = AppUtils.getFolderSize(subDir)

        if (fileSize > 2L * 1024 * 1024 * 1024) {
            sizeLimitReached()
            return@launch
        }

        val request = Request.Builder().url(fileUrl).build()
        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) {
            onFailure(response.message)
            return@launch
        }

        response.body?.byteStream()?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
    }

    Timber.d("Storage File saved at: ${file.absolutePath}")

    return file.absolutePath.toUri()
}