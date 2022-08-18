package com.cpy.tkaccessibility.accessibility.base

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.TargetApi
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.cpy.tkaccessibility.accessibility.base.contact.ITiKaAccessibilityService
import com.cpy.tkaccessibility.accessibility.base.contact.ITiKaAccessibilitySubProxy
import kotlinx.coroutines.*

abstract class TikaAccessibilitySubProxy : ITiKaAccessibilitySubProxy {

    protected var mProxy: TiKaAccessibilityProxy? = null

    protected var mRealService: AccessibilityService? = null

    protected var mIdle = true

    protected val mExtrasData = mutableMapOf<String, Any>()

    protected var mCoroutineScope = CoroutineScope(Dispatchers.Main)
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

    override fun getExtrasData(): MutableMap<String, Any> = mExtrasData

    override fun putExtraData(extraData: MutableMap<String, Any>) {
        mExtrasData.clear()
        mExtrasData.putAll(extraData)
    }

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


    //输入x, y坐标模拟点击事件
    @TargetApi(Build.VERSION_CODES.N)
    fun performXYClick(x: Float, y: Float) {
        mProxy?.getAccessibilityService()?.apply {
            val path = Path()
            path.moveTo(x, y)
            val builder = GestureDescription.Builder()
            builder.addStroke(GestureDescription.StrokeDescription(path, 0, 5L))
            val gestureDescription = builder.build()
            dispatchGesture(
                gestureDescription,
                object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription) {
                        super.onCompleted(gestureDescription)
                        Log.d("Accessibility", "Gesture onCompleted: ")
                    }

                    override fun onCancelled(gestureDescription: GestureDescription) {
                        super.onCancelled(gestureDescription)
                        Log.d("Accessibility", "Gesture onCancelled: ")
                    }
                },
                null
            )
        }

    }

    //模拟滑动事件
    @TargetApi(Build.VERSION_CODES.N)
    fun swipe(x1: Float, y1: Float, x2: Float, y2: Float): Unit {
        mProxy?.getAccessibilityService()?.apply {
            val path = Path()
            path.moveTo(x1, y1)
            path.lineTo(x2, y2)
            val builder = GestureDescription.Builder()
            builder.addStroke(GestureDescription.StrokeDescription(path, 0, 500L))
            val gestureDescription = builder.build()
            dispatchGesture(
                gestureDescription,
                object : AccessibilityService.GestureResultCallback() {
                    override fun onCompleted(gestureDescription: GestureDescription) {
                        super.onCompleted(gestureDescription)
                        Log.d("Accessibility", "Gesture onCompleted: ")
                    }

                    override fun onCancelled(gestureDescription: GestureDescription) {
                        super.onCancelled(gestureDescription)
                        Log.d("Accessibility", "Gesture onCancelled: ")
                    }
                },
                null
            )
        }
    }

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