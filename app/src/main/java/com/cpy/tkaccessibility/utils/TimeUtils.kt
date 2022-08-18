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


fun Date.afterNow1m(): Boolean {
    return (this.time - Date().time) > TimeUnit.MINUTES.toMillis(1)
}