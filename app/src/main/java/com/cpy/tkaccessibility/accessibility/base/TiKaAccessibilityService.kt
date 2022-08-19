package com.cpy.tkaccessibility.accessibility.base

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.cpy.tkaccessibility.AccessibilityMode
import com.cpy.tkaccessibility.accessibility.base.contact.ITiKaAccessibilityService
import com.cpy.tkaccessibility.toast
import java.util.concurrent.CopyOnWriteArrayList

val tkServices = CopyOnWriteArrayList<AccessibilityMode>()

class TiKaAccessibilityService : AccessibilityService(), ITiKaAccessibilityService {

    override fun onServiceConnected() {
        super.onServiceConnected()
        toast("服务已连接")
        Log.d("Accessibility", "cpy ==> onServiceConnected: ")
    }

    /**
     * TYPE_WINDOW_STATE_CHANGED 页面activity发生变化，event.className会带上activity类名
     * as:com.pupumall.webview.page.BannerWebViewActivity、com.pupumall.customer.activity.MainPresenterActivity
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("Accessibility", "onAccessibilityEvent ${event?.className}, type=${event?.eventType}")
        // 获取事件类型，在对应的事件类型中对相应的节点进行操作
        when (event?.eventType) {
            //当通知栏发生改变时
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ->
                Log.d("Accessibility", "onAccessibilityEvent TYPE_NOTIFICATION_STATE_CHANGED}")

            //当窗口的状态发生改变时（界面改变）
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ->
                Log.d("Accessibility", "onAccessibilityEvent TYPE_WINDOW_STATE_CHANGED}")

            //内容改变
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ->
                Log.d("Accessibility", "onAccessibilityEvent TYPE_WINDOW_CONTENT_CHANGED}")

            //滑动变化
            AccessibilityEvent.TYPE_VIEW_SCROLLED ->
                Log.d("Accessibility", "onAccessibilityEvent TYPE_VIEW_SCROLLED}")

        }


        tkServices.forEach {
            if (!it.service.idleState()) {
                it.service.onAccessibilityEvent(event, this)
            }
        }
    }

    override fun onInterrupt() {
        Log.d("Accessibility", "cpy ==> onInterrupt: ")
        tkServices.forEach {
            if (!it.service.idleState()) {
                it.service.onInterrupt(this)
            }
        }
    }


    override fun getCurrService(): AccessibilityService {
        return this
    }

    override fun onUnbind(intent: Intent?): Boolean {
        tkServices.forEach {
            it.service.dispose()
        }
        return super.onUnbind(intent)
    }

}