package com.mostone.tikaaccessibility.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.mostone.tikaaccessibility.AppApplication

fun getAppContext(): Context = AppApplication.context

fun isAccessibilityEnabled(): Boolean {
    val context = getAppContext()
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
    return accessibilityManager?.isEnabled ?: false
}

@Suppress("DEPRECATION")
fun isAccessibilitySettingsOn(className: String): Boolean {
    val context = getAppContext()
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val runningServices = activityManager?.getRunningServices(100) ?: return false
    for (service in runningServices) {
        if (service::class.java.name == className) {
            return true
        }
    }
    return false
}


/**
 * 前往开启辅助服务界面
 */
fun goAccess() {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    AppApplication.context.startActivity(intent)
}