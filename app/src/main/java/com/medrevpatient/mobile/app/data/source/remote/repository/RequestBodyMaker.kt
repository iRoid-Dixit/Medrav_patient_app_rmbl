package com.medrevpatient.mobile.app.data.source.remote.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class RequestBodyMaker @Inject constructor() {

    fun addLogRequestBody(
        goalType: String,
        value: String,
        imageFiles: List<File>
    ): Pair<HashMap<String, RequestBody>, List<MultipartBody.Part>> {


        val params = hashMapOf<String, RequestBody>()

        // Add goalType and value to the map
        params["type"] = goalType.toRequestBody("text/plain".toMediaTypeOrNull())
        params["value"] = value.toRequestBody("text/plain".toMediaTypeOrNull())

        // Prepare MultipartBody.Part list for images
        val imageParts = imageFiles.map { file ->
            val requestFile = file.asRequestBody("image/${file.extension}".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        Timber.d("addLog: $params")
        return Pair(params, imageParts)
    }
}