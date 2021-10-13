package com.mostone.tikaaccessibility.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import com.mostone.tikaaccessibility.AppApplication


open class BaseAccessibilityService : AccessibilityService() {
    private val mAccessibilityManager: AccessibilityManager by lazy(LazyThreadSafetyMode.NONE) {
        AppApplication.context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
    }
    private val mContext: Context by lazy(LazyThreadSafetyMode.NONE) {
        AppApplication.context
    }


    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    protected fun checkAccessibilityEnabled(serviceName: String): Boolean {
        val accessibilityServices =
            mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        for (info in accessibilityServices) {
            if (info.id == serviceName) {
                return true
            }
        }
        return false
    }

    /**
     * 模拟点击事件
     *
     * @param node nodeInfo
     */
    protected fun performViewClick(node: AccessibilityNodeInfo?) {
        var nodeInfo = node
        if (nodeInfo == null) {
            return
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                break
            }
            nodeInfo = nodeInfo.parent
        }
    }

    /**
     * 模拟返回操作
     */
    protected fun performBackClick() {
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /**
     * 模拟下滑操作
     */
    protected fun performScrollBackward() {
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
    }

    /**
     * 模拟上滑操作
     */
    protected fun performScrollForward() {
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
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
    ): AccessibilityNodeInfo? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null && nodeInfo.isClickable == clickable) {
                    return nodeInfo
                }
            }
        }
        return null
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    protected fun findViewByID(id: String): AccessibilityNodeInfo? {
        val accessibilityNodeInfo = rootInActiveWindow ?: return null
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo
                }
            }
        }
        return null
    }

    protected fun clickTextViewByText(text: String) {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo)
                    break
                }
            }
        }
    }

    protected fun clickTextViewByID(id: String) {
        val accessibilityNodeInfo = rootInActiveWindow ?: return
        val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            for (nodeInfo in nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo)
                    break
                }
            }
        }
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    protected fun inputText(nodeInfo: AccessibilityNodeInfo, text: String) {
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}
}