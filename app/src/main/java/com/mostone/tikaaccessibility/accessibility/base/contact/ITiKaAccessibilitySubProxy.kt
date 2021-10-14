package com.mostone.tikaaccessibility.accessibility.base.contact

import android.view.accessibility.AccessibilityEvent

interface ITiKaAccessibilitySubProxy {

    fun onAccessibilityEvent(event: AccessibilityEvent?, service: ITiKaAccessibilityService)

    fun onInterrupt(service: ITiKaAccessibilityService)

    /**
     * 是否处于空闲状态
     */
    fun idleState(): Boolean

    /**
     * 切换启用状态
     */
    fun switchIdleState()
}