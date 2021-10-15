package com.mostone.tikaaccessibility.accessibility.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.mostone.tikaaccessibility.accessibility.base.contact.ITiKaAccessibilityService
import com.mostone.tikaaccessibility.accessibility.base.contact.ITiKaAccessibilitySubProxy
import kotlinx.coroutines.*

abstract class TikaAccessibilitySubProxy : ITiKaAccessibilitySubProxy {

    private var mProxy: TiKaAccessibilityProxy? = null

    protected var mRealService: AccessibilityService? = null

    protected var mIdle = true

    protected val mExtrasData = mutableMapOf<String, Any?>()

    private var mCoroutineScope = CoroutineScope(Dispatchers.Main)
        get() {
            if (!field.isActive) {
                field = CoroutineScope(Dispatchers.Main)
            }
            return field
        }

    private val mViewActionJobMap = mutableMapOf<String, Job?>()

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

    override fun getExtrasData(): MutableMap<String, Any?> = mExtrasData

    override fun switchIdleState() {
        mIdle = !mIdle
        if (mIdle) {
            mCoroutineScope.cancel()
        }
    }

    override fun dispose() {
        mCoroutineScope.cancel()
        mViewActionJobMap.clear()
    }

    private fun init(service: ITiKaAccessibilityService) {
        if (service != this.mRealService || mProxy == null) {
            mProxy = TiKaAccessibilityProxy(service)
        }
        this.mRealService = service.getCurrService()
    }

    protected fun registerAction(key: String, block: suspend CoroutineScope.() -> Unit) {
        val oldJob = mViewActionJobMap[key]
        oldJob?.cancel()
        mViewActionJobMap[key] = mCoroutineScope.launch {
            block(this)
        }
    }

    protected fun runInViewTarget(
        event: AccessibilityEvent?,
        target: String,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (event?.className == target) {
            registerAction(target, block)
        }
    }

    protected fun unregisterAction(key: String) {
        mViewActionJobMap[key]?.cancel()
        mViewActionJobMap.remove(key)
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

    protected fun performScrollBackward(node: AccessibilityNodeInfo?) {
        mProxy?.performScrollBackward(node)
    }

    /**
     * 模拟上滑操作
     */
    protected fun performScrollForward() {
        mProxy?.performScrollForward()
    }

    protected fun performScrollForward(node: AccessibilityNodeInfo?) {
        mProxy?.performScrollForward(node)
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