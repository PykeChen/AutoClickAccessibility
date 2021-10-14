package com.mostone.tikaaccessibility.accessibility.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.mostone.tikaaccessibility.accessibility.base.contact.ITiKaAccessibilityService
import com.mostone.tikaaccessibility.accessibility.base.contact.ITiKaAccessibilitySubProxy

abstract class TikaAccessibilitySubProxy : ITiKaAccessibilitySubProxy {

    private var mProxy: TiKaAccessibilityProxy? = null

    protected var mRealService: AccessibilityService? = null

    protected var mIdle = true

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?,
        service: ITiKaAccessibilityService
    ) {
        init(service)
        onAccessibilityEvent(event)
    }

    override fun onInterrupt(service: ITiKaAccessibilityService) {
        init(service)
        onIntercept()
    }

    override fun idleState(): Boolean {
        return mIdle
    }

    override fun switchIdleState() {
        mIdle = !mIdle
    }

    private fun init(service: ITiKaAccessibilityService) {
        if (service != this.mRealService || mProxy == null) {
            mProxy = TiKaAccessibilityProxy(service)
        }
        this.mRealService = service.getCurrService()
    }

    /**
     * 模拟点击事件
     *
     * @param node nodeInfo
     */
    protected fun performViewClick(node: AccessibilityNodeInfo?) {
        mProxy?.performViewClick(node)
    }

    /**
     * 模拟返回操作
     */
    protected fun performBackClick() {
        mProxy?.performBackClick()
    }

    /**
     * 模拟下滑操作
     */
    protected fun performScrollBackward() {
        mProxy?.performScrollBackward()
    }

    /**
     * 模拟上滑操作
     */
    protected fun performScrollForward() {
        mProxy?.performScrollForward()
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    protected fun findViewByText(
        text: String,
        clickable: Boolean = false
    ): AccessibilityNodeInfo? = mProxy?.findViewByText(text, clickable)

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    protected fun findViewByID(id: String): AccessibilityNodeInfo? = mProxy?.findViewByID(id)

    protected fun clickTextViewByText(text: String) = mProxy?.clickTextViewByText(text)

    protected fun clickTextViewByID(id: String) = mProxy?.clickTextViewByID(id)

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    protected fun inputText(nodeInfo: AccessibilityNodeInfo, text: String) =
        mProxy?.inputText(nodeInfo, text)


    abstract fun onAccessibilityEvent(event: AccessibilityEvent?)

    open fun onIntercept() {}
}