package com.cpy.tkaccessibility.utils

import android.app.Activity
import android.app.Service
import android.content.Context
import android.os.PowerManager
import android.view.WindowManager


/**
 * WakeHelper
 * @author cpy
 * Created on 2022-08-18 14:07
 */
object WakeHelper {


    private var wakLock: PowerManager.WakeLock? = null

    // --------------------------------------------------------------
    // <editor-fold defaultState="collapsed" desc="Companion">
    // --------------------------------------------------------------
    /**
     * 打开休眠锁只能保持手机不休眠
     * @param context
     */
    fun openWakeLock(context: Context) {
        if (wakLock != null && wakLock!!.isHeld) {
            return
        }
        wakLock?.release()
        val powerManager = context.getSystemService(Service.POWER_SERVICE) as PowerManager
        wakLock =
            powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AutoClick:LockTag").apply {
                //是否需计算锁的数量
                setReferenceCounted(false)
                //请求常亮，onResume()
                acquire(60 * 60 * 1000L /*60 minutes*/)
            }

    }

    fun releaseLock() {
        wakLock?.release()
        wakLock = null
    }
    // </editor-fold>


    fun keepScreenState(on: Boolean, activity: Activity) {
        if (on) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }
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