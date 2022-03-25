package com.guillaumewilmot.swoleai.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    fun withFormat(
        timestamp: Long,
        format: String,
        userLocale: Locale,
        timezone: String? = null,
    ): String = getFormatter(strict = false, format, userLocale, timezone).format(timestamp)

    fun withFormatStrict(
        timestamp: Long,
        format: String,
        userLocale: Locale,
        timezone: String? = null,
    ): String = getFormatter(strict = true, format, userLocale, timezone).format(timestamp)

    fun withFormat(
        date: Date,
        format: String,
        userLocale: Locale,
        timezone: String? = null,
    ): String = getFormatter(strict = false, format, userLocale, timezone).format(date)

    fun withFormatStrict(
        date: Date,
        format: String,
        userLocale: Locale,
        timezone: String? = null,
    ): String = getFormatter(strict = true, format, userLocale, timezone).format(date)

    fun Date.plusDays(days: Int): Date = Calendar.getInstance().let {
        it.time = this
        it.add(Calendar.DATE, days)
        it
    }.time

    fun Date.minusDays(days: Int): Date = Calendar.getInstance().let {
        it.time = this
        it.add(Calendar.DATE, -days)
        it
    }.time

    fun Date.isSameWeek(date: Date): Boolean {
        val calendar = Calendar.getInstance().apply {
            time = this@isSameWeek
        }
        val otherCalendar = Calendar.getInstance().apply {
            time = date
        }
        return calendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.WEEK_OF_YEAR) == otherCalendar.get(Calendar.WEEK_OF_YEAR)
    }

    private fun getFormatter(
        strict: Boolean,
        givenFormat: String,
        userLocale: Locale,
        timezone: String?
    ): SimpleDateFormat {
        val format = if (strict) givenFormat else try {
            DateFormat.getBestDateTimePattern(userLocale, givenFormat)
                ?: givenFormat
        } catch (e: Exception) {
            givenFormat
        }

        return SimpleDateFormat(format, userLocale).apply {
            applyLocalizedPattern(toLocalizedPattern())
            timezone?.let {
                timeZone = TimeZone.getTimeZone(timezone)
            }
        }
    }

    const val DATE_FORMAT_DAY_OF_WEEK_SHORT = "E"
    const val DATE_FORMAT_FULL_DATE = "EEEE d MMMM yyyy"
}