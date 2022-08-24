package com.cpy.tkaccessibility.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * TimeTask
 * @author cpy
 * Created on 2022-08-16 17:55
 */
object TimeUtils {

    fun time2Date(date: Date): String? {
        val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
        return  return simpleDateFormat.format(date)
    }

    fun time2DateSeconds(date: Date): String? {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun time2Date(timeStr: String): Date? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try {
            return simpleDateFormat.parse(timeStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun timeBeforeNow1m(value: Date): Boolean {
        return (value.time - Date().time) > TimeUnit.MINUTES.toMillis(1)
    }

    fun adjustTime(start: Date, mills:Long): Date {
        return Date(start.time + mills)
    }

    fun adjustTime(oneHour: Boolean): Date {
        val result = Calendar.getInstance().apply {
            if (oneHour) {
                val hour = get(Calendar.HOUR)
                set(Calendar.HOUR, hour + 1)
                set(Calendar.MINUTE, 0)
            } else {
                set(Calendar.MINUTE, 30)
            }
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return result.time
    }
    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Companion">
    // --------------------------------------------------------------

    // </editor-fold>


    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Fields">
    // --------------------------------------------------------------

    // </editor-fold>


    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Override Methods">
    // --------------------------------------------------------------

    // </editor-fold>

    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Define Methods">
    // --------------------------------------------------------------

    // </editor-fold>

    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Inner and Anonymous Classes">
    // --------------------------------------------------------------

    // </editor-fold>

    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Getter & Setter">
    // --------------------------------------------------------------

    // </editor-fold>
}


fun Date.afterNow10S(): Boolean {
    return (this.time - Date().time) > TimeUnit.SECONDS.toMillis(10)
}