package com.medrevpatient.mobile.app.utils

//noinspection ExifInterface
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.aviran.cookiebar2.CookieBar
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object AppUtils {

    //Get device id
    @SuppressLint("HardwareIds")
    fun getDeviceId(c: Context): String {
        return try {
            Settings.Secure.getString(c.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            Logger.e("exception: ${e.message}")
            ""
        }
    }

    fun formatDateTime(timestamp: String): String {
        return try {
            val millis = timestamp.toLongOrNull() ?: return "Invalid Date"

            val date = Date(millis) // Convert milliseconds to Date

            // Output format: "June 12 2024 - 10:00 AM"
            val outputFormat = SimpleDateFormat("MMMM dd yyyy - hh:mm a", Locale.getDefault())
            outputFormat.format(date) // Format Date to desired string
        } catch (e: Exception) {
            Logger.e("Invalid Date: ${e.message}")
            "Invalid Date"
        }
    }

    fun formatTimestamp(timestamp: Long?): String {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            sdf.format(timestamp)
        } else {
            ""
        }
    }

    fun getFileFromContentUri(context: Context, contentUri: Uri, filename: String): File? {
        val inputStream = context.contentResolver.openInputStream(contentUri)
        inputStream?.let {
            //making directory
            val cacheDir = context.cacheDir
            val gapMediaDir = File(cacheDir, Constants.AppInfo.DIR_NAME)
            if (!gapMediaDir.exists()) {
                gapMediaDir.mkdir()
            }

            val mimeType = getFileExtensionFromUri(context = context, uri = contentUri)

            // Create a new file with a unique name
            val tempFile = File.createTempFile(filename, ".$mimeType", gapMediaDir)
            // Copy the content of the input stream to the file using buffered streams
            val outputStream: OutputStream = FileOutputStream(tempFile)
            val bufferedInputStream = BufferedInputStream(inputStream)
            val bufferedOutputStream = BufferedOutputStream(outputStream)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (bufferedInputStream.read(buffer).also { bytesRead = it } != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead)
            }
            bufferedOutputStream.flush()

            // Close the streams
            bufferedInputStream.close()
            bufferedOutputStream.close()
            return tempFile
        }
        return null
    }
    fun formatStorageSize(kb: Double?): String {
        val size = kb ?: 0.0
        return when {
            size >= 1_000_000 -> String.format(Locale.US, "%.2f GB", size / 1_000_000)
            size >= 1_000 -> String.format(Locale.US, "%.2f MB", size / 1_000)
            else -> String.format(Locale.US, "%.2f KB", size)
        }
    }

    fun getTimeAgo(timestampMillis: Long): String {
        val now = Instant.now()
        val past = Instant.ofEpochMilli(timestampMillis)
        val duration = Duration.between(past, now)

        return when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} mins ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            duration.toDays() == 1L -> "Yesterday"
            duration.toDays() < 7 -> "${duration.toDays()} days ago"
            else -> {
                val localDateTime = past.atZone(ZoneId.systemDefault()).toLocalDate()
                localDateTime.toString() // e.g. 2024-04-07
            }
        }
    }

    fun createMultipartBody(file: File?, keyName: String?): MultipartBody.Part {
        return if (file != null) {
            MultipartBody.Part.createFormData(
                keyName!!,
                file.name,
                file.asRequestBody(getMimeType(file.toString())?.toMediaTypeOrNull())
            )
        } else {
            MultipartBody.Part.createFormData(
                keyName!!,
                "",
                "".toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }
    }
    fun createMultipartBody(
        file: ArrayList<File?>,
        keyName: String?
    ): ArrayList<MultipartBody.Part> {
        var value: MultipartBody.Part?
        val valueArray: ArrayList<MultipartBody.Part> = arrayListOf()
        for (i in file) {
            value = if (i != null) {
                MultipartBody.Part.createFormData(
                    keyName!!,
                    i.name,
                    i.asRequestBody(getMimeType(i.toString())?.toMediaTypeOrNull())
                )
            } else {
                MultipartBody.Part.createFormData(
                    keyName!!,
                    "",
                    "".toRequestBody("text/plain".toMediaTypeOrNull())
                )
            }
            valueArray.add(value)
        }
        return valueArray
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver
        // Get the mime type of the content URI
        val mimeType: String? = contentResolver.getType(uri)
        // Extract the file extension from the mime type
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
    //Get address from latitude and longitude


    /**
     * Helps to set clickable part in text.
     *
     * Don't forget to set android:textColorLink="@color/link" (click selector) and
     * android:textColorHighlight="@color/window_background" (background color while clicks)
     * in the TextView where you will use this.
     */


    fun showWaringMessage(context: Context, message: String) {
        CookieBar.build(context as Activity)
            .setTitle("Legacy Cache App")
            .setMessage(message)
            .setBackgroundColor(R.color.warning_colors)
            .setCookiePosition(CookieBar.TOP)
            .show()
    }

    fun showErrorMessage(context: Context, message: String) {
        CookieBar.build(context as Activity)
            .setTitle("Legacy Cache App")
            .setMessage(message)
            .setBackgroundColor(R.color.error_colors)
            .setCookiePosition(CookieBar.TOP)
            .show()
    }

    fun showSuccessMessage(context: Context, message: String) {
        CookieBar.build(context as Activity)
            .setTitle("Legacy Cache App")
            .setMessage(message)
            .setBackgroundColor(R.color.success_colors)
            .setCookiePosition(CookieBar.TOP)
            .setDuration(2000)
            .show()
    }

    fun getCurrentTimeInSeconds(): Long {
        return System.currentTimeMillis().div(1000)
    }

    fun convertDateToTimestamp(dateString: String): Long {
        val formats = listOf("dd/MM/yyyy", "MMM dd yyyy", "yyyy-MM-dd")

        for (format in formats) {
            try {
                val dateFormat = SimpleDateFormat(format, Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("UTC") // ✅ Fix here
                val date = dateFormat.parse(dateString)
                if (date != null) return date.time
            } catch (_: Exception) {
                // Ignore and try next format
            }
        }

        Log.e("TAG", "Error parsing date: $dateString")
        return 0L
    }

    fun convertTimestampToDate(timestamp: Long): String {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = Date(timestamp)
            dateFormat.format(date)
        } catch (e: Exception) {
            Log.e("TAG", "Error converting timestamp to date: $timestamp", e)
            ""
        }
    }
    fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
        this then Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
    }
    fun Context.isScreenLocked(): Boolean {
        val keyguardManager: KeyguardManager? =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        val powerManager: PowerManager? = getSystemService(Context.POWER_SERVICE) as PowerManager?
        val locked = keyguardManager != null && keyguardManager.isKeyguardLocked
        val interactive = powerManager != null && powerManager.isInteractive
        return locked || !interactive
    }

}



