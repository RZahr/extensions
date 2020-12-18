package com.rzahr.extensions

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

fun Date?.isWithinXDays(days: Long): Boolean {

    val calendar = Calendar.getInstance()
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    calendar.time = this
    val calendar2 = Calendar.getInstance()
    calendar2.time = Date()
    return TimeUnit.DAYS.convert(calendar2.time.time - calendar.time.time, TimeUnit.MILLISECONDS) <= days
}

fun Date?.isWithinWeek(): Boolean {

    val calendar = Calendar.getInstance()
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    calendar.time = this
    val calendar2 = Calendar.getInstance()
    calendar2.time = Date()
    return calendar.get(Calendar.WEEK_OF_MONTH) == calendar2.get(Calendar.WEEK_OF_MONTH)
}

fun Date?.isWithinMonth(): Boolean {

    val calendar = Calendar.getInstance()
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    calendar.time = this
    val calendar2 = Calendar.getInstance()
    calendar2.time = Date()
    return calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
}

const val DASHED_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val SLASHED_FORMAT = "dd/MM/yyyy hh:mm:ss a"

fun String.toDate(format: String = DASHED_FORMAT): Date? = SimpleDateFormat(format, Locale.ENGLISH).parse(
    this
)

@TargetApi(Build.VERSION_CODES.O)
fun String.toLocalDate(format: String = DASHED_FORMAT): LocalDate {
    val formatter =   DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
    return LocalDate.parse(this, formatter)
}

/**
 * returns a string date
 * @param format the format
 * @return a date string
 */
fun Date.asDateString(format: String = DASHED_FORMAT): String {

    val calendar = Calendar.getInstance()
    calendar.time = this
    return SimpleDateFormat(format, Locale.ENGLISH).format(calendar.time)
}

/**
 * get the current date in a string format
 * @param english the language if english
 * @param format the format
 * @return a date string
 */
@SuppressLint("SimpleDateFormat")
fun getCurrentDate(english: Boolean = true, format: String = DASHED_FORMAT): String {

    val now = Date()
    return if (english)
        SimpleDateFormat(format, Locale.ENGLISH).format(now)
    else
        SimpleDateFormat(format).format(now)
}

/**
 * changes the time to string
 * @param format the format
 * @param timeZone the timezone
 * @return date string
 */
fun Long.asDateString(format: String = SLASHED_FORMAT, timeZone: TimeZone? = null): String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
    if (timeZone != null) simpleDateFormat.timeZone = timeZone
    return simpleDateFormat.format(Date(this).time)
}