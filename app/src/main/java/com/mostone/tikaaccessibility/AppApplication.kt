package com.mostone.tikaaccessibility

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings

class AppApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        /**
         * 前往开启辅助服务界面
         */
        fun goAccess() {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }


}