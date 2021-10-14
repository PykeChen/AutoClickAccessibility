package com.mostone.tikaaccessibility.accessibility.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.mostone.tikaaccessibility.AccessibilityMode
import com.mostone.tikaaccessibility.accessibility.base.contact.ITiKaAccessibilityService
import java.util.concurrent.CopyOnWriteArrayList

val tkServices = CopyOnWriteArrayList<AccessibilityMode>()

class TiKaAccessibilityService : AccessibilityService(), ITiKaAccessibilityService {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        tkServices.forEach {
            if (!it.service.idleState()) {
                it.service.onAccessibilityEvent(event, this)
            }
        }
    }

    override fun onInterrupt() {
        tkServices.forEach {
            if (!it.service.idleState()) {
                it.service.onInterrupt(this)
            }
        }
    }

    override fun getCurrService(): AccessibilityService {
        return this
    }

}