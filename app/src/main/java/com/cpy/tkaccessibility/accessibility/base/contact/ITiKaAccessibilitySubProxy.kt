package com.mostone.tikaaccessibility.accessibility.base.contact

import android.view.accessibility.AccessibilityEvent

interface ITiKaAccessibilitySubProxy {

    fun onAccessibilityEvent(event: AccessibilityEvent?, service: ITiKaAccessibilityService)

    fun onInterrupt(service: ITiKaAccessibilityService)

    fun getExtrasData(): MutableMap<String, Any>

    fun putExtraData(extraData: MutableMap<String, Any>)

    /**
     * 是否处于空闲状态
     */
    fun idleState(): Boolean

    /**
     * 切换启用状态
     */
    fun switchIdleState()

    fun dispose()
}