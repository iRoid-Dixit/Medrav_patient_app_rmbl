@file:Suppress("unused")

package com.medrevpatient.mobile.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.text.format.Time
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private const val TAG = "DateTimeUtils"
    private const val today = "Today"
    private const val yesterday = "Yesterday"

    /*
    * enum - date & time formats
    */
    @Suppress("unused", "SpellCheckingInspection", "EnumEntryName")
    enum class DateFormats(val label: String) {
        dd("dd"),
        MM("MM"),
        MMM("MMM"),
        MMMM("MMMM"),
        yy("yy"),
        yyyy("yyyy"),
        HH("HH"),
        mm("mm"),
        ss("ss"),
        EEEE("EEEE"),
        HHmmsssss("HH:mm:ss.SSS'Z'"),
        HHmmss("HH:mm:ss"),
        HHmm("HH:mm"),
        hmm("h:mm"),
        hmma("h:mm a"),
        hhmma("hh:mm a"),
        a("a"),
        yyyyMMdd("yyyy-MM-dd"),
        ddMMMyyyy("dd MMM yyyy"),
        ddMMyy("dd-MM-yy"),
        ddMMyyyy("dd-MM-yyyy"),
        ddMMyy_slashed("dd/MM/yy"),
        ddMMyyyy_slashed("dd/MM/yyyy"),
        MMddyyyy_slashed("MM/dd/yyyy"),
        ddMM_slashed("dd/MM"),
        ddMMMyyyy_slashed("dd MMM yyyy"),
        MMMMddyyyy("MMMM dd, yyyy"),
        MMMdyyyy("MMM d yyyy"),
        MMMdyyyyComma("MMM d, yyyy"),
        MMMd("MMM d"),
        dMMMMyyyy("d MMMM yyyy"),
        EEEMMMddyyyy("EEE, MMM dd, yyyy"),
        EEEMMMddyyyyhmma(EEEMMMddyyyy.label + " " + hmma.label),
        hmmaMMMdyyyy(hmma.label + ", " + MMMdyyyy.label),
        yyyyMMddhhmma(yyyyMMdd.label + " " + hmma.label),
        yyyyMMddHHmmss(yyyyMMdd.label + " " + HHmmss.label),
        ddMMyyyyHHmmss(ddMMyyyy_slashed.label + " " + HHmmss.label),
        MMddyyyyHHmmss(MMddyyyy_slashed.label + " " + HHmmss.label),
        EEEEMMMdyyyyhhmma(EEEE.label + ", " + MMMdyyyy.label + " " + hmma.label),
        yyyyMMddTHHmmsssss(yyyyMMdd.label + "'T'" + HHmmsssss.label),
        ddMMMyyyyhhmma("dd MMM, yyyy HH:mm"),
        ddMMMyyyyHHmm_pipe("${dd.label} ${MMM.label} ${yyyy.label} | ${HH.label}:${mm.label}");

    }

    /**
     * convert date&time to UTC
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @param timeZone
     * @return
     */
    fun formatDateTimeToUTC(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?,
        timeZone: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            ) || TextUtils.isEmpty(timeZone)
        ) "" else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getTimeZone(timeZone)
            val date = sourceFormat.parse(sourceString!!)
            val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            targetFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert date&time to UTC
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun formatDateTimeToUTCTimeStamp(sourceString: String?, sourceDateFormat: String?): Long {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat))
            0
        else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString!!)
            date!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }


    /**
     * Convert date & time to UTC timestamp
     *
     * @param sourceDate The source date to convert to UTC
     * @return The UTC timestamp in milliseconds
     */
    fun Date.formatDateTimeToUTCTimeStamp(): Long {
        return time.div(1000)
    }

    /**
     * convert date&time to UTC
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @return
     */
    fun formatDateTimeToUTC(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) ""
        else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getDefault()
            val date = sourceFormat.parse(sourceString!!)
            val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            targetFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun formatDateTimeToUTC(sourceString: String?, sourceDateFormat: String?): Date {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) Date() else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getDefault()
            val date = sourceFormat.parse(sourceString!!)
            val calendar: Calendar = Calendar.getInstance()
            val tz: TimeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            return Date(date!!.time)
        } catch (e: ParseException) {
            e.printStackTrace()
            Date()
        }
    }


    /**
     * convert date&time to UTC
     *
     * @param sourceDate
     * @param targetDateFormat
     * @return
     */
    fun formatDateTimeToUTC(sourceDate: Date?, targetDateFormat: String?): String {
        return if (TextUtils.isEmpty(targetDateFormat)) "" else try {
            val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            targetFormat.format(sourceDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * convert UTC into date
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @param timeZone
     * @return
     */
    fun formatUTCToDateTime(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?,
        timeZone: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            ) || TextUtils.isEmpty(timeZone)
        ) "" else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString!!)
            val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.timeZone = TimeZone.getTimeZone(timeZone)
            targetFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun formatUTCToDateTime(sourceString: String?, sourceDateFormat: String?): Date {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) Date() else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString!!)
            val calendar: Calendar = Calendar.getInstance()
            val tz: TimeZone = TimeZone.getDefault()
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            return Date(date!!.time)
        } catch (e: ParseException) {
            e.printStackTrace()
            Date()
        }
    }

    /**
     * convert UTC into date
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @return
     */
    fun formatUTCToDateTime(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) "" else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
            sourceFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString!!)
            val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun formatUTCToDate(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        return if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) "" else try {
            val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
//            sourceFormat.timeZone = TimeZone.getTimeZone(Time.TIMEZONE_UTC)
            val date = sourceFormat.parse(sourceString!!)
            val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
            targetFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun formatUTCToDateTime(timeStamp: Long?, targetDateFormat: String?): String {
        if (timeStamp!! == 0L) return ""
        try {
            val calendar: Calendar = Calendar.getInstance()
            val tz: TimeZone = TimeZone.getDefault()
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            val sdf = SimpleDateFormat(targetDateFormat, Locale.getDefault())
            val currentTimeZone = Date(timeStamp * 1000)
            return sdf.format(currentTimeZone)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @SuppressLint("SimpleDateFormat")
    fun formatUTCToDateTimeNew(timeStamp: Long?, targetDateFormat: String?): String {
        if (timeStamp!! == 0L) return ""
        try {
            val tz: TimeZone = TimeZone.getDefault()//UTC
            val sdf = SimpleDateFormat(targetDateFormat)
            sdf.timeZone = tz
            val currentTimeZone = Date(timeStamp * 1000)
            return sdf.format(currentTimeZone)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun formatUTCToDateTime(timeStamp: Long?): Date? {
        if (timeStamp!! == 0L) return null
        try {
            val calendar: Calendar = Calendar.getInstance()
            val tz: TimeZone = TimeZone.getDefault()
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
            return Date(timeStamp * 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * convert string into particular date&time format
     *
     * @param sourceString
     * @param sourceDateFormat
     * @param targetDateFormat
     * @return
     */
    fun formatDateTime(
        sourceString: String?,
        sourceDateFormat: String?,
        targetDateFormat: String?
    ): String {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat) || TextUtils.isEmpty(
                targetDateFormat
            )
        ) {
            return ""
        }
        val sourceFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
        var date: Date? = null
        try {
            date = sourceFormat.parse(sourceString!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
        return targetFormat.format(date!!)
    }

    /**
     * convert date into particular date&time format
     *
     * @param sourceDate
     * @param targetDateFormat
     * @return
     */
    fun formatDateTime(sourceDate: Date?, targetDateFormat: String?): String {
        if (sourceDate == null || TextUtils.isEmpty(targetDateFormat)) {
            return ""
        }
        val targetFormat = SimpleDateFormat(targetDateFormat, Locale.US)
        return targetFormat.format(sourceDate)
    }

    /**
     * convert string into date
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun formatDateTime(sourceString: String?, sourceDateFormat: String?): Date? {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) {
            return null
        }
        val targetFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
        var targetDate: Date? = null
        try {
            targetDate = targetFormat.parse(sourceString!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return targetDate
    }

    @SuppressLint("SimpleDateFormat")
    fun formatUtcToDateTimestamp(utcDateString: String, sourceDateFormat: String): Long {
        return try {
            // Step 1: Parse the UTC date string into a Date object
            val sdf = SimpleDateFormat(sourceDateFormat)
            sdf.timeZone = TimeZone.getDefault()//UTC
            val date = sdf.parse(utcDateString)

            // Step 2: Convert the Date object to a timestamp value
            date?.time ?: -1L
        } catch (e: Exception) {
            e.printStackTrace()
            -1L // Return -1 to indicate an error in parsing the date
        }
    }

    /**
     * check datetime string is in given format
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun validFormat(sourceString: String?, sourceDateFormat: String?): Boolean {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) {
            return false
        }
        val targetFormat = SimpleDateFormat(sourceDateFormat, Locale.US)
        try {
            targetFormat.parse(sourceString!!)
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /* get time difference into minutes
    *
    * @param sourceDate
    * @param targetDate
    * @ return
    */
    fun timeDifferenceInMinutes(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toMinutes(difference))
        return TimeUnit.MILLISECONDS.toMinutes(difference)
    }

    /**
     * get time difference into seconds
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInSeconds(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toSeconds(difference))
        return TimeUnit.MILLISECONDS.toSeconds(difference)
    }

    /**
     * get time difference into hours
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInHours(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toHours(difference))
        return TimeUnit.MILLISECONDS.toHours(difference)
    }

    /**
     * get time difference into days
     *
     * @param sourceDate
     * @param targetDate
     * @return
     */
    fun timeDifferenceInDays(sourceDate: Date, targetDate: Date): Long {
        val difference = sourceDate.time - targetDate.time
        //Log.e(TAG, "Time difference " + TimeUnit.MILLISECONDS.toDays(difference))
        return TimeUnit.MILLISECONDS.toDays(difference)
    }

    /**
     * check given date to today
     *
     * @param sourceDate
     * @return
     */
    fun isToday(sourceDate: Date?): Boolean {
        if (sourceDate == null) {
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.time = sourceDate
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val todayCalendar = Calendar.getInstance()
        todayCalendar[Calendar.HOUR_OF_DAY] = 0
        todayCalendar[Calendar.MINUTE] = 0
        todayCalendar[Calendar.SECOND] = 0
        todayCalendar[Calendar.MILLISECOND] = 0
        return calendar.time == todayCalendar.time
    }

    /**
     * check given date to today
     *
     * @param sourceDate
     * @return
     */
    fun isTomorrow(sourceDate: Date?): Boolean {
        if (sourceDate == null) {
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.time = sourceDate
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        val tomorrowCalendar = Calendar.getInstance()
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1)
        tomorrowCalendar[Calendar.HOUR_OF_DAY] = 0
        tomorrowCalendar[Calendar.MINUTE] = 0
        tomorrowCalendar[Calendar.SECOND] = 0
        tomorrowCalendar[Calendar.MILLISECOND] = 0
        return calendar.time == tomorrowCalendar.time
    }

    /**
     * check given date's year is current year
     *
     * @param date
     * @return
     */
    fun isCurrentYear(date: String?, sourceDateFormat: String): Boolean {
        if (date == null) {
            return false
        }
        val sourceDate = formatDateTime(date, sourceDateFormat)
        val calendar = Calendar.getInstance()
        if (sourceDate != null) {
            calendar.time = sourceDate
        }
        val todayCalendar = Calendar.getInstance()
        return calendar[Calendar.YEAR] == todayCalendar[Calendar.YEAR]
    }

    /**
     * get day from date
     *
     * @param sourceDate
     * @return
     */
    fun getDay(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.dd.label, sourceDate) as String
    }

    /**
     * get month from date
     *
     * @param sourceDate
     * @return
     */
    fun getMonth(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.MM.label, sourceDate) as String
    }

    /**
     * get year from date
     *
     * @param sourceDate
     * @return
     */
    fun getYear(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.yyyy.label, sourceDate) as String
    }

    /**
     * get year from date
     *
     * @param sourceDate
     * @return
     */
    fun getYearInTwoDigit(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.yy.label, sourceDate) as String
    }

    /**
     * get weekday from date
     *
     * @param sourceDate
     * @return
     */
    fun getWeekDay(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.EEEE.label, sourceDate) as String
    }

    /**
     * get time from date
     *
     * @param sourceDate
     * @return
     */
    fun getTime(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.hmma.label, sourceDate) as String
    }

    /**
     * get hour from date
     *
     * @param sourceDate
     * @return
     */
    fun getHour(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.HH.label, sourceDate) as String
    }

    /**
     * get minute from date
     *
     * @param sourceDate
     * @return
     */
    fun getMinute(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.mm.label, sourceDate) as String
    }

    /**
     * get second from date
     *
     * @param sourceDate
     * @return
     */
    fun getSecond(sourceDate: Date?): String {
        return DateFormat.format(DateFormats.ss.label, sourceDate) as String
    }

    /**
     * get minute - with in 15 minute time slot
     *
     * @param minute
     * @return
     */
    fun getRoundedMinute(minute: Int): Int {
        return when (minute) {
            in 0..14 -> {
                15
            }

            in 15..29 -> {
                30
            }

            in 30..44 -> {
                45
            }

            else -> {
                0
            }
        }
    }

    /**
     * get timestamp with particular timezone
     *
     * @param sourceString
     * @param sourceDateFormat
     * @return
     */
    fun getTimeStampWithTimezone(sourceString: String, sourceDateFormat: String?): String {
        if (TextUtils.isEmpty(sourceString) || TextUtils.isEmpty(sourceDateFormat)) {
            return ""
        }
        val pickUpDateTime = formatDateTime(sourceString.trim { it <= ' ' }, sourceDateFormat)
        return "/Date(" + pickUpDateTime!!.time + "+0530" + ")/"
    }


    /**
     * Formats the [pattern] correctly for the current locale, and replaces 12 hour format with
     * 24 hour format if necessary
     */
    private fun Context.getFormatter(pattern: String): SimpleDateFormat {
        var formattedPattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern)

        if (DateFormat.is24HourFormat(this)) {
            formattedPattern = formattedPattern.replace("h", "HH").replace("K", "HH")
//                .replace(" a".toRegex(), "")
        }

        return SimpleDateFormat(formattedPattern, Locale.getDefault())
    }

    fun Context.getDetailedTimestamp(date: Long): String {
        return getFormatter("M/d/y, h:mm:ss a").format(date)
    }

    fun Context.getTimestamp(date: Long): String {
        return getFormatter("h:mm a").format(date)
    }

    /*fun getMessageTimestamp(date: Long, context: Context): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = date

        return when {
            now.isSameDay(then) -> getFormatter("h:mm a", context).format(Date(date))
            now.isSameWeek(then) -> getFormatter("E, h:mm a", context).format(Date(date))
            now.isSameYear(then) -> getFormatter("MMM d, h:mm a", context).format(Date(date))
            else -> getFormatter("MMM d yyyy, h:mm a", context).format(Date(date))
        }
    }*/
    fun getMessageTimestamp(date: Long, context: Context): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = date
        return when {
            now.isSameDay(then) -> "Today"
            now.isYesterday(then) -> "Yesterday"
            //    now.isSameWeek(then) -> getFormatter("E, h:mm a", context).format(Date(date))
            now.isSameYear(then) -> getFormatter("MMM d yyyy", context).format(Date(date))
            else -> getFormatter("MMM d yyyy", context).format(Date(date))
        }
    }
    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return current.format(formatter)
    }

    fun Context.getConversationTimestamp(date: Long): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = date
        return if (now.isYesterday(then)) "Yesterday"
        else when {
            now.isSameDay(then) -> getFormatter("h:mm a")
            now.isSameWeek(then) -> getFormatter("E")
            now.isSameYear(then) -> getFormatter("MMM d")
            else -> getFormatter("MM/d/yy")
        }.format(date)
    }

    fun Context.getScheduledTimestamp(date: Long): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = date

        return when {
            now.isSameDay(then) -> getFormatter("h:mm a")
            now.isSameYear(then) -> getFormatter("MMM d h:mm a")
            else -> getFormatter("MMM d yyyy h:mm a")
        }.format(date)
    }

    fun getDateLabel(date: Long, context: Context): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = date

        return when {
            now.isSameDay(then) -> today
            now.isYesterday(then) -> yesterday
            else -> getFormatter(DateFormats.MMMMddyyyy.label, context).format(date)
        }
    }

    /**
     * Formats the [pattern] correctly for the current locale, and replaces 12 hour format with
     * 24 hour format if necessary
     */
    fun getFormatter(pattern: String, context: Context): SimpleDateFormat {
        var formattedPattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern)

        if (DateFormat.is24HourFormat(context)) {
            formattedPattern = formattedPattern
                .replace("h", "HH")
                .replace("K", "HH")
//                .replace(" a".toRegex(), "")
        }

        return SimpleDateFormat(formattedPattern, Locale.getDefault())
    }


    private fun Calendar.isSameDay(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(
            Calendar.DAY_OF_YEAR
        )
    }

    private fun Calendar.isYesterday(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) - 1 == other.get(
            Calendar.DAY_OF_YEAR
        )
    }

    fun Calendar.isSameWeek(other: Calendar): Boolean {
        val weekOfYear = this.get(Calendar.WEEK_OF_YEAR)
        val year = this.get(Calendar.YEAR)

        val otherWeekOfYear = other.get(Calendar.WEEK_OF_YEAR)
        val otherYear = other.get(Calendar.YEAR)

        return weekOfYear == otherWeekOfYear && year == otherYear
    }

    private fun Calendar.isSameYear(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR)
    }

    fun getLongFromDateString(date: String, format: SimpleDateFormat): Long {
        val parseDate: Date = format.parse(date) as Date
        return parseDate.time
    }
    /*
       class func formatSecondsToString(_ seconds: TimeInterval) -> String {
            if seconds.isNaN {
                return "00:00"
            }
            let Min = Int(seconds / 60)
            let Sec = Int(seconds.truncatingRemainder(dividingBy: 60))
            return String(format: "%02d:%02d", Min, Sec)
        }*/

    fun formatDurationFromSeconds(seconds: Double): String {
        return if (seconds == 0.0) {
            "00:00"
        } else DateUtils.formatElapsedTime(seconds.toLong())

    }

    fun convertMillieToHMmSs(millie: Long): String {
        val seconds = millie / 1000
        val second = seconds % 60
        val minute = seconds / 60 % 60
        val hour = seconds / (60 * 60) % 24
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

    fun atStartOfDay(date: Date): Date {
        val localDateTime: LocalDateTime = dateToLocalDateTime(date)
        val startOfDay: LocalDateTime = localDateTime.with(LocalTime.MIN)
        return localDateTimeToDate(startOfDay)
    }

    fun atEndOfDay(date: Date): Date {
        val localDateTime: LocalDateTime = dateToLocalDateTime(date)
        val endOfDay: LocalDateTime = localDateTime.with(LocalTime.MAX)
        return localDateTimeToDate(endOfDay)
    }

    private fun dateToLocalDateTime(date: Date): LocalDateTime {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
    }

    private fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = (24 * HOUR_MILLIS).toLong()
    private const val WEEK_MILLIS = 7 * DAY_MILLIS
    private const val MONTH_MILLIS = 30 * DAY_MILLIS
    private const val YEAR_MILLIS = 12 * MONTH_MILLIS
    fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun getTimeAgo(date: Date?): String {
        date?.let {
            var time = date.time
            if (time < 1000000000000L) {
                time *= 1000
            }

            val now = currentDate().time
            if (time > now || time <= 0) {
                return "in the future"
            }

            val diff = now - time
            return when {
                diff < MINUTE_MILLIS -> "${diff / SECOND_MILLIS} second ago"
                diff < 2 * MINUTE_MILLIS -> "a minute ago"
                diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
                diff < 2 * HOUR_MILLIS -> "an hour ago"
                diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
                diff < 48 * HOUR_MILLIS -> "yesterday"
                diff < 7 * DAY_MILLIS -> "${diff / DAY_MILLIS} days ago"
                diff < 2 * WEEK_MILLIS -> "${diff / WEEK_MILLIS} week ago"
                diff < 3 * WEEK_MILLIS -> "${diff / WEEK_MILLIS} weeks ago"
                diff < 2 * MONTH_MILLIS -> "a month ago"
                diff < 12 * MONTH_MILLIS -> "${diff / MONTH_MILLIS} months ago"
                diff < 2 * YEAR_MILLIS -> "a year ago"
                else -> "${diff / YEAR_MILLIS} years ago"
            }
        } ?: return ""
    }

    fun getCurrentUtcDateTime(): Date {
        //val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        return calendar.time
    }

    fun Date.getTimeInSeconds(): Long {
        return time.div(1000)
    }

    fun timeAgoSince(date: String, isSender: Boolean, tick: Int): String {

        if (date.isBlank()) {
            return ""
        }
        val sent = "Sent"
        val seen = "Seen"

        // val time = formatUTCToDateTime(date , yyyyMMddHHmmss.label, DateFormats.yyyyMMddTHHmmsssss.label)
        val dateFormat = SimpleDateFormat(DateFormats.yyyyMMddHHmmss.label, Locale.US)
        val pasTime = dateFormat.parse(date)
        val nowTime = getCurrentUtcDateTime()
        val dateDiff = nowTime.time - pasTime.time
        val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val day = TimeUnit.MILLISECONDS.toDays(dateDiff)

        Log.e(TAG, "timeAgoSince: NOW : $nowTime :: PAST : $pasTime")
        if (day >= 7) {
            val week = day / 7
            return if (week >= 2) {
                if (isSender) {
                    if (tick == 2) seen else sent
                } else {
                    "${week}w"
                }

            } else {
                if (isSender) {
                    if (tick == 2) "$seen Last week" else "$sent Last week"
                } else {
                    "${(day / 7)}w"
                }
            }
        }
        if (day >= 2) {
            return if (isSender) {
                if (tick == 2) "$seen ${getWeekDay(pasTime)}" else "$sent ${getWeekDay(pasTime)}"
            } else {
                "${day}d"
            }

        }
        if (day >= 1) {
            return if (isSender) {
                if (tick == 2) "$seen Yesterday" else "$sent Yesterday"
            } else {
                "${day}d"
            }
        }
        if (hour in 1..23) {
            return if (isSender) {
                if (tick == 2) "$seen ${hour}h ago" else "$sent ${hour}h ago"
            } else {
                "${hour}h"
            }
        }
        if (minute in 1..59) {
            return if (isSender) {
                if (tick == 2) "$seen ${minute}m ago" else "$sent ${minute}m ago"
            } else {
                "${minute}m"
            }
        }


        return if (isSender) {
            if (tick == 2) "$seen Just now" else "$sent Just now"
        } else {
            "Just now"
        }
    }

    fun getPastThreeYears(): Array<String> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        val pastYears = mutableListOf<String>()
        for (i in 0 until 3) {
            pastYears.add((currentYear - i).toString())
        }

        return pastYears.toTypedArray()
    }

    //displays date like Fri, 4th Aug 2023
    fun formatDateWithOrdinalFormat(date: Date): String {
        val sdf = SimpleDateFormat("EEE, d'${getDayOfMonthSuffix(date)}' MMMM")
        return sdf.format(date)
    }

    fun getDayOfMonthSuffix(date: Date): String {
        val day = SimpleDateFormat("d").format(date).toInt()
        return when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }
}