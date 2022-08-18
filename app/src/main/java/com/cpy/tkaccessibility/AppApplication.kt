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

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        context = base
    }


}