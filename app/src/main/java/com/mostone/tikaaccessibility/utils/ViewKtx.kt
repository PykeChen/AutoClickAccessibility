package com.mostone.tikaaccessibility.utils

import android.os.SystemClock
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.gyf.immersionbar.ktx.immersionBar

fun FragmentActivity.initImmersionBar(topView: View) {
    immersionBar {
        titleBar(topView)
        statusBarDarkFont(true)
        navigationBarColor(android.R.color.white)
        autoNavigationBarDarkModeEnable(true)
    }
}

fun Fragment.initImmersionBar(topView: View){
    immersionBar {
        titleBar(topView)
        statusBarDarkFont(true)
        navigationBarColor(android.R.color.white)
        autoNavigationBarDarkModeEnable(true)
    }
}

fun View.debounceClick(action: () -> Unit) {
    val actionDebounce = ActionDebounce(action, 1000, false)
    setOnClickListener {
        actionDebounce.notifyAction()
    }
}

private class ActionDebounce(
    private val action: () -> Unit,
    private val intervalTime: Long?,
    private val isGlobalClickTime: Boolean
) {

    companion object {
        const val DEBOUNCE_INTERVAL_MILLISECONDS = 600L
    }

    private var lastActionTime = 0L

    fun notifyAction() {
        val now = SystemClock.elapsedRealtime()
        val millisecondsPassed = now - lastActionTime
        val actionAllowed = millisecondsPassed > (intervalTime ?: DEBOUNCE_INTERVAL_MILLISECONDS)

        if (actionAllowed) {
            lastActionTime = now
            action.invoke()
        }
    }
}