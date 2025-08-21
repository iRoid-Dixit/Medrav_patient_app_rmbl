@file:Suppress("DEPRECATION")

package com.medrevpatient.mobile.app.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color.WHITE
import android.graphics.Color.parseColor
import android.location.Geocoder
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import com.medrevpatient.mobile.app.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

object AppUtils {


    fun formatTimeToHHMMSSInt(seconds: Long): String {
        return String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            seconds / 3600,
            (seconds % 3600) / 60,
            seconds % 60
        )
    }

    fun deleteInternalStorageCaches(ctx: Context): Boolean {
        return ctx.cacheDir.deleteRecursively()
    }

    fun getFolderSize(folder: File): Long {
        var size: Long = 0
        if (folder.exists()) {
            folder.listFiles()?.forEach { file ->
                size += if (file.isFile) file.length() else getFolderSize(file) // Recursively calculate size
            }
        }
        return size
    }

    fun formatSize(sizeInBytes: Long): String {
        val kb = sizeInBytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gb)
            mb >= 1 -> String.format(Locale.getDefault(), "%.2f MB", mb)
            kb >= 1 -> String.format(Locale.getDefault(), "%.2f KB", kb)
            else -> "$sizeInBytes Bytes"
        }
    }


    fun Uri.toFile(context: Context): File? {
        val inputStream = context.contentResolver.openInputStream(this)
        val tempFile = File.createTempFile("temp", ".jpg")
        return try {
            tempFile.outputStream().use { fileOut ->
                inputStream?.copyTo(fileOut)
            }
            tempFile.deleteOnExit()
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    fun Int.formatNumberInK(): String {
        return when {
            this >= 1_000_000 -> {
                val formatted = this / 1_000_000.0
                val isZeroDecimal = (formatted * 10).toInt() % 10 <= 0
                if (isZeroDecimal) "${formatted.toInt()}M" else String.format(
                    Locale.getDefault(),
                    "%.1fM",
                    formatted
                )
            }

            this >= 1_000 -> {
                val formatted = this / 1_000.0
                val isZeroDecimal = (formatted * 10).toInt() % 10 <= 0
                if (isZeroDecimal) "${formatted.toInt()}K" else String.format(
                    Locale.getDefault(),
                    "%.1fK",
                    formatted
                )
            }

            else -> this.toString()
        }
    }

    fun getFormatedTimeBySeconds(totalSec: Long): String {
        val hour = totalSec / 3600
        val min = (totalSec % 3600) / 60
        val remainingSec = totalSec % 60
        return if (hour > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, remainingSec)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", min, remainingSec)
        }
    }

    fun Toast(context: Context, message: String, duration: Int = 0): Toast {
        // Create a new Toast object
        val toast = Toast(context)

        // Inflate a custom view for the Toast
        val toastView = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color(0xFF252525).toArgb()) // Set background color to black
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
        }

        // Add a TextView to display the message
        val messageTextView = TextView(context).apply {
            text = message
            setTextColor(WHITE) // Set text color to white
            textSize = 16f // Set text size
            typeface = ResourcesCompat.getFont(context, R.font.outfit_semibold)
            gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                56.dpToPx(context) // Ensure the height is 56dp
            ).apply {
                setMargins(
                    16.dpToPx(context),
                    0,
                    16.dpToPx(context),
                    0
                ) // Add padding for better appearance
            }
        }

        // Add the TextView to the parent layout
        toastView.addView(messageTextView)

        // Set the custom view for the Toast
        toast.view = toastView

        // Position the Toast at the bottom center
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)

        // Set duration
        toast.duration = Toast.LENGTH_SHORT

        return toast
    }

    // Extension function to convert dp to pixels
    fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }

    //Get device id
    @SuppressLint("HardwareIds")
    fun getDeviceId(c: Context): String {
        return try {
            Settings.Secure.getString(c.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            ""
        }
    }

    //Convert dp to pixel
    fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return px.roundToInt()
    }

    //Get address from latitude and longitude
    fun getAddress(latitude: String, longitude: String, context: Context): String {
        val geocoder = Geocoder(context)
        val addressList = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        addressList?.let {
            if (addressList.size > 0) {
                val address = StringBuffer()
                val addressData = addressList[0]
                if (addressData.locality != null) {
                    address.append(addressData.locality).append(", ")
                }
                address.append(addressData.adminArea)

                return address.toString()
            }
        }
        return ""
    }

    /**
     * Helps to set clickable part in text.
     *
     * Don't forget to set android:textColorLink="@color/link" (click selector) and
     * android:textColorHighlight="@color/window_background" (background color while clicks)
     * in the TextView where you will use this.
     */
    fun SpannableString.withClickableSpan(
        context: Context,
        clickablePart: String,
        onClickListener: () -> Unit
    ): SpannableString {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = onClickListener.invoke()
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = parseColor("#000000")
//                ds.typeface = ResourcesCompat.getFont(context, R.font.public_sans_bold)
                ds.isUnderlineText = false
            }
        }
        val clickablePartStart = indexOf(clickablePart)
        setSpan(
            clickableSpan,
            clickablePartStart,
            clickablePartStart + clickablePart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return this
    }

    fun getCurrentTimeInSeconds(): Long {
        return System.currentTimeMillis().div(1000)
    }


    fun getDateAndMonth(inputDate: String): Pair<String, String> {

        if (inputDate.isEmpty()) {
            return Pair("", "")
        }

        // Input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        // Parse the input string into a Date object
        val date = inputFormat.parse(inputDate)

        // Output formats for date and month
        val dayFormat = SimpleDateFormat("dd", Locale.ENGLISH) // For day
        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH) // For short month name

        val day = date?.let { dayFormat.format(it) } // Extract day as "02"
        val month = date?.let { monthFormat.format(it) } // Extract month as "Dec"

        return Pair(day ?: "", month ?: "")
    }

    fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
        this then Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        )
    }

    fun convertToMilliseconds(hours: Int, minutes: Int, seconds: Int): Long {
        return (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L)
    }

    @SuppressLint("DefaultLocale")
    fun formatDuration(durationMillis: Long): String {
        val totalSeconds = durationMillis / 1000 // Convert milliseconds to seconds
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun createMultipartBodyForFile(
        file: File?,
        keyName: String,
        imeType: String = "*"
    ): MultipartBody.Part {
        return if (file != null) {
            MultipartBody.Part.createFormData(
                keyName,
                file.name,
                file.asRequestBody("*/$imeType".toMediaTypeOrNull())
            )
        } else {
            MultipartBody.Part.createFormData(
                keyName,
                "",
                "".toRequestBody("text/plain".toMediaTypeOrNull())
            )
        }
    }

    // Function to get the file extension from a content URI
    /**
     * TODO function for getting extension from uri
     *
     * @param context - activity context
     * @param uri
     * @return
     */
    fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.contentResolver

        // Get the mime type of the content URI
        val mimeType: String? = contentResolver.getType(uri)

        // Extract the file extension from the mime type
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        val fileName = getFileNameFromContentUri(uri, context)
        val tempFile = File(context.cacheDir, fileName)
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            return null
        }
        return tempFile
    }

    private fun getFileNameFromContentUri(contentUri: Uri, context: Context): String {
        var fileName = ""
        val cursor: Cursor? = context.contentResolver?.query(contentUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }

    fun convertToLocalDate(dateString: String): LocalDate {
        // Parse the date string to ZonedDateTime first
        val zonedDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)

        // Convert ZonedDateTime to LocalDate
        return zonedDateTime.toLocalDate()
    }

    fun convertLongToLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun formatTimeStampForBirthDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTimeStampForBarChart(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    @SuppressLint("DefaultLocale")
    fun convertSecondsToHoursMin(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val second = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, second)
    }

    @SuppressLint("DefaultLocale")
    fun convertSecondsToOnlyHours(seconds: Long): String {
        val hours = seconds / 3600
        return String.format("%02d Hrs", hours)
    }

    fun formatTimestampForComments(timestamp: Long): String {
        val localTimeZone = TimeZone.getDefault()  // Get device's local timezone
        val sdfDate = SimpleDateFormat("d MMM,yy", Locale.getDefault()).apply {
            timeZone = localTimeZone
        }
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).apply {
            timeZone = localTimeZone
        }

        val currentDate = Calendar.getInstance(localTimeZone) // Ensure it's in local timezone
        val timestampDate = Calendar.getInstance(localTimeZone).apply {
            timeInMillis = timestamp
        }

        return if (sdfDate.format(currentDate.time) == sdfDate.format(timestampDate.time)) {
            sdfTime.format(timestampDate.time)
                .uppercase(Locale.getDefault())  // Show time if it's today
        } else {
            sdfDate.format(timestampDate.time)  // Show date otherwise
        }
    }

    fun formatTimestampForMyPosts(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTimestampForPostCreation(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just Now"
            minutes < 60 -> "$minutes Min${if (minutes > 1) "s" else ""} Ago"
            hours < 24 -> "$hours Hr${if (hours > 1) "s" else ""} Ago"
            days < 7 -> "$days Day${if (days > 1) "s" else ""} Ago"
            else -> {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            }
        }
    }

    fun createMultipartBody(
        file: List<Uri?>,
        keyName: String?,
        context: Context
    ): List<MultipartBody.Part?> {
        var valueArray: List<MultipartBody.Part?> = emptyList()
        for (i in file) {
            val fileExtension = i?.let { getFileExtensionFromUri(context, it) }
            val files = i?.let { getFileFromUri(context, it) }
            valueArray = valueArray + fileExtension?.let {
                createMultipartBodyForFile(
                    files,
                    keyName!!,
                    imeType = it
                )
            }
        }
        return valueArray
    }

    fun getStartDateOfMonth(month: Int, year: Int): String {
        val startDate = LocalDate.of(year, month, 1)
        return startDate.format(DateTimeFormatter.ISO_DATE)
    }

    fun getEndDateOfMonth(month: Int, year: Int): String {
        val endDate = LocalDate.of(year, month, 1)
            .withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
        return endDate.format(DateTimeFormatter.ISO_DATE)
    }

    @SuppressLint("ConstantLocale")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    fun getMonthFromDate(dateString: String): Int {
        return try {
            LocalDate.parse(dateString, dateFormatter).monthValue
        } catch (e: Exception) {
            e.printStackTrace()
            LocalDate.now().monthValue  // Fallback to current month
        }
    }

    fun getYearFromDate(dateString: String): Int {
        return try {
            LocalDate.parse(dateString, dateFormatter).year
        } catch (e: Exception) {
            e.printStackTrace()
            LocalDate.now().year  // Fallback to current year
        }
    }
}