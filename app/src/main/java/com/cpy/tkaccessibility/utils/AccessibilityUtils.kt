package com.mostone.tikaaccessibility.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.accessibility.AccessibilityManager
import com.mostone.tikaaccessibility.AppApplication
import com.mostone.tikaaccessibility.accessibility.base.TiKaAccessibilityService


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

private val ACCESSIBILITY_SERVICE_PATH: String? = TiKaAccessibilityService::class.java.canonicalName


/**
 * 判断是否有辅助功能权限
 *
 * @param context
 * @return
 */
fun isAccessibilitySettingsOn(): Boolean {
    val context = getAppContext()
    var accessibilityEnabled = 0
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            context.applicationContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
    } catch (e: SettingNotFoundException) {
        e.printStackTrace()
    }
    val packageName = context.packageName
    val serviceStr = "$packageName/$ACCESSIBILITY_SERVICE_PATH"
    Log.d("cpy", "isAccessibilitySettingsOn: $serviceStr")
    if (accessibilityEnabled == 1) {
        val mStringColonSplitter = SimpleStringSplitter(':')
        val settingValue = Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessabilityService = mStringColonSplitter.next()
                if (accessabilityService.equals(serviceStr, ignoreCase = true)) {
                    return true
                }
            }
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