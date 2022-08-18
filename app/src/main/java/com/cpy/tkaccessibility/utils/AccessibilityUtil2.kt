package com.cpy.tkaccessibility.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.TargetApi
import android.app.Notification
import android.app.PendingIntent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 1.查询类
 * findOneByClazz 按类名寻找节点和子节点内的一个匹配项
 * findAllByClazz 按类名寻找节点和子节点内的所有匹配项
 * findFrontNode 查找节点的前兄弟节点
 * findBackNode 查找节点的后兄弟节点
 * findCanScrollNode 返回可滚动元素集合
 * findOneByDesc 按描述寻找节点和子节点内的一个匹配项
 * findOneByText 按文本(关键词)寻找节点和子节点内的一个匹配项
 * findAllByText 按文本(关键词)寻找节点和子节点内的所有匹配项
 *
 * 2.全局操作
 * globalGoBack 回退
 * globalGoHome 回桌面
 *
 * 3.窗口操作
 * printNodeClazzTree 深度搜索打印节点及其子节点
 * performScrollUp 对某个节点向上滚动 未生效
 * performScrollDown 对某个节点向下滚动 未生效
 * performClick 对某个节点进行点击
 * performXYClick 输入x, y坐标模拟点击事件
 * editTextInput 编辑EditView(非粘贴 推荐)
 * findTextAndClick 寻找第一个文本匹配(关键词)并点击
 * findTextInput 寻找第一个EditView编辑框并输入文本
 * findListOneAndClick 寻找第一个列表并点击指定条目(默认点击第一个条目)
 * scrollAndFindByText 滚动并按文本寻找第一个控件
 * performClickWithSon 对某个节点或子节点进行点击
 * performLongClick 对某个节点或父节点进行长按
 * performLongClickWithSon 对某个节点或子节点进行长按
 *
 */
object AccessibilityUtil2 {
    private const val tag = "AccessibilityUtil"

    //编辑EditView(非粘贴 推荐)
    fun editTextInput(nodeInfo: AccessibilityNodeInfo?, text: String): Boolean {
        val nodeInfo: AccessibilityNodeInfo = nodeInfo ?: return false
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        return true
    }

    //寻找第一个文本匹配(关键词)并点击
    fun findTextAndClick(nodeInfo: AccessibilityNodeInfo?, text: String): Boolean {
        val textView = findOneByText(nodeInfo, text) ?: return false
        return performClick(textView)
    }

    //寻找第一个EditView编辑框并输入文本
    fun findTextInput(nodeInfo: AccessibilityNodeInfo?, text: String): Boolean {
        val nodeInfo: AccessibilityNodeInfo = nodeInfo ?: return false
        val editText = findOneByClazz(nodeInfo, "android.widget.EditText") ?: return false
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        return true
    }

    //寻找第一个列表并点击指定条目(默认点击第一个条目)
    fun findListOneAndClick(nodeInfo: AccessibilityNodeInfo, index: Int = 0): Boolean {
        val rv = findOneByClazz(nodeInfo, "androidx.recyclerview.widget.RecyclerView")
        val lv = findOneByClazz(nodeInfo, "android.widget.ListView")
        if (rv == null && lv == null) return false
        if (rv != null && rv.childCount > index) {
            performClick(rv.getChild(index))
        } else if (lv != null && lv.childCount > index) {
            performClick(lv.getChild(index))
        }
        return true
    }

    //滚动并按文本寻找第一个控件
    fun scrollAndFindByText(
        nodeInfo: AccessibilityNodeInfo,
        text: String,
        maxRetry: Int = 5
    ): AccessibilityNodeInfo? {
        var index = 0
        while (index++ < maxRetry) {
            performScrollUp(nodeInfo, 0)
            Thread.sleep(100)
            val node = findOneByText(nodeInfo, text)
            if (node != null) {
                return node
            }
        }
        while (index++ < maxRetry * 2) {
            performScrollDown(nodeInfo, 0)
            Thread.sleep(100)
            val node = findOneByText(nodeInfo, text)
            if (node != null) {
                return node
            }
        }
        return null
    }

    //输入x, y坐标模拟点击事件
    @TargetApi(Build.VERSION_CODES.N)
    fun performXYClick(service: AccessibilityService, x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        val builder = GestureDescription.Builder()
        builder.addStroke(GestureDescription.StrokeDescription(path, 0, 1))
        val gestureDescription = builder.build()
        service.dispatchGesture(
            gestureDescription,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                    //Log.i(Constant.TAG, "onCompleted: completed");
                }

                override fun onCancelled(gestureDescription: GestureDescription) {
                    super.onCancelled(gestureDescription)
                    //Log.i(Constant.TAG, "onCancelled: cancelled");
                }
            },
            null
        )
    }

    /**
     * 对某个节点或父节点进行点击
     */
    fun performClick(nodeInfo: AccessibilityNodeInfo?): Boolean {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return false
        while (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            }
            nodeInfo = nodeInfo.parent
        }
        return false
    }

    /**
     * 对某个节点或子节点进行点击
     */
    fun performClickWithSon(nodeInfo: AccessibilityNodeInfo?): Boolean {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return false
        while (nodeInfo != null) {
            if (nodeInfo.isClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            }
            if (nodeInfo.childCount > 0) {
                for (i in 0 until nodeInfo.childCount) {
                    if (performClickWithSon(nodeInfo.getChild(i))) {
                        return true
                    }
                }
            } else {
                nodeInfo = null
            }
        }
        return false
    }

    /**
     * 对某个节点或父节点进行长按
     */
    fun performLongClick(nodeInfo: AccessibilityNodeInfo?): Boolean {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return false
        while (nodeInfo != null) {
            if (nodeInfo.isLongClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
                return true
            }
            nodeInfo = nodeInfo.parent
        }
        return false
    }

    /**
     * 对某个节点或子节点进行长按
     */
    fun performLongClickWithSon(nodeInfo: AccessibilityNodeInfo?): Boolean {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return false
        while (nodeInfo != null) {
            if (nodeInfo.isLongClickable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
                return true
            }
            if (nodeInfo.childCount > 0) {
                for (i in 0 until nodeInfo.childCount) {
                    if (performLongClickWithSon(nodeInfo.getChild(i))) {
                        return true
                    }
                }
            } else {
                nodeInfo = null
            }
        }
        return false
    }

    //对某个节点向上滚动
    fun performScrollUp(nodeInfo: AccessibilityNodeInfo?): Boolean {
        var nodeInfo: AccessibilityNodeInfo? = nodeInfo ?: return false
        while (nodeInfo != null) {
            if (nodeInfo.isScrollable) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
                return true
            }
            nodeInfo = nodeInfo.parent
        }
        return false
    }

    //对某个节点或父节点向下滚动
    fun performScrollDown(node: AccessibilityNodeInfo?): Boolean {
        var node: AccessibilityNodeInfo? = node ?: return false
        while (node != null) {
            if (node.isScrollable) {
                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                return true
            }
            node = node.parent
        }
        return false
    }

    //对第几个节点向上滚动
    fun performScrollUp(node: AccessibilityNodeInfo?, index: Int): Boolean {
        if (node == null) return false
        val canScrollNodeList = findCanScrollNode(node)
        if (canScrollNodeList.size > index) {
            canScrollNodeList[index].performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
            return true
        }
        return false
    }

    //对第几个节点向下滚动
    fun performScrollDown(node: AccessibilityNodeInfo?, index: Int): Boolean {
        if (node == null) return false
        val canScrollNodeList = findCanScrollNode(node)
        if (canScrollNodeList.size > index) {
            canScrollNodeList[index].performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
            return true
        }
        return false
    }

    //返回可滚动元素集合
    fun findCanScrollNode(
        node: AccessibilityNodeInfo?,
        list: ArrayList<AccessibilityNodeInfo> = ArrayList()
    ): ArrayList<AccessibilityNodeInfo> {
        if (node == null) return list
        if (node.isScrollable) list.add(node)
        for (i in 0 until node.childCount) {
            findCanScrollNode(node.getChild(i), list)
        }
        return list
    }

    //通知栏事件进入应用
    fun gotoApp(event: AccessibilityEvent) {
        val data = event.parcelableData
        if (data != null && data is Notification) {
            val intent = data.contentIntent
            try {
                intent.send()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }
        }
    }

    //回退
    fun globalGoBack(service: AccessibilityService) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    //回首页
    fun globalGoHome(service: AccessibilityService) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }

    //按描述寻找节点和子节点内的一个匹配项
    fun findOneByDesc(
        node: AccessibilityNodeInfo?,
        desc: String,
        desc2: String? = null
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        val description = node.contentDescription?.toString()
        if (description == desc || (desc2 != null && description == desc2)) {
            return node
        }
        for (i in 0 until node.childCount) {
            val result = findOneByDesc(node.getChild(i), desc, desc2)
            if (result != null) return result
        }
        return null
    }

    //按文本(关键词)寻找节点和子节点内的一个匹配项
    fun findOneByText(
        node: AccessibilityNodeInfo?,
        text: String,
        text2: String? = null
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        val textViewList = node.findAccessibilityNodeInfosByText(text)
        if (textViewList != null && textViewList.size > 0)
            return textViewList[0]
        else if (text2 != null)
            return findOneByText(node, text2)
        return null
    }

    //按文本(关键词)寻找节点和子节点内的所有匹配项
    fun findAllByText(
        node: AccessibilityNodeInfo?,
        text: String,
        text2: String? = null
    ): List<AccessibilityNodeInfo> {
        if (node == null) return arrayListOf()
        val textViewList = node.findAccessibilityNodeInfosByText(text)
        if (textViewList != null && textViewList.size > 0)
            return textViewList
        else if (text2 != null)
            return findAllByText(node, text2)
        return arrayListOf()
    }

    /**
     * 按类名寻找节点和子节点内的一个匹配项
     * node 节点
     * clazz 类名
     * limitDepth 深度 限制深度搜索深度必须匹配提供值且类名相同才返回 不填默认不限制
     */
    fun findOneByClazz(
        node: AccessibilityNodeInfo?,
        clazz: String,
        limitDepth: Int? = null,
        depth: Int = 0
    ): AccessibilityNodeInfo? {
        if (node == null) return null
//        Log.d(tag, "node.className: " + node.className)
        if (node.className == clazz) {
            if (limitDepth == null || limitDepth == depth)
                return node
        }
        for (i in 0 until node.childCount) {
            val result = findOneByClazz(node.getChild(i), clazz, limitDepth, depth + 1)
            if (result != null) return result
        }
        return null
    }

    /**
     * 按类名寻找节点和子节点内的所有匹配项
     * node 节点
     * clazz 类名
     * limitDepth 深度 限制深度搜索深度必须匹配提供值且类名相同才返回 不填默认不限制
     */
    fun findAllByClazz(
        node: AccessibilityNodeInfo?,
        clazz: String,
        list: ArrayList<AccessibilityNodeInfo> = ArrayList()
    ): ArrayList<AccessibilityNodeInfo> {
        if (node == null) return list
//        Log.d(tag, "node.className: " + node.className)
        if (node.className == clazz) list.add(node)
        for (i in 0 until node.childCount) {
            findAllByClazz(node.getChild(i), clazz, list)
        }
        return list
    }

    /**
     * 查找节点的前兄弟节点
     * node 节点
     */
    fun findFrontNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
//        Log.d(tag, "node.className: " + node.className)
        var parent: AccessibilityNodeInfo? = node.parent
        var son: AccessibilityNodeInfo? = node
        while (parent != null) {
            var index = -1
            for (i in 0 until parent.childCount) {
                if (parent.getChild(i) == son) {
                    index = i
                    break
                }
            }
            if (index > 0) {
                return parent.getChild(index - 1)
            }
            son = parent
            parent = parent.parent
        }
        return null
    }

    /**
     * 查找节点的后兄弟节点
     * node 节点
     */
    fun findBackNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
//        Log.d(tag, "node.className: " + node.className)
        var parent: AccessibilityNodeInfo? = node.parent
        var son: AccessibilityNodeInfo? = node
        while (parent != null) {
            var index = -1
            for (i in 0 until parent.childCount) {
                if (parent.getChild(i) == son) {
                    index = i
                    break
                }
            }
            if (index < parent.childCount - 1) {
                return parent.getChild(index + 1)
            }
            son = parent
            parent = parent.parent
        }
        return null
    }

    /**
     * 深度搜索打印节点及其子节点
     * node 节点
     */
    fun printNodeClazzTree(
        node: AccessibilityNodeInfo?,
        printText: Boolean = true,
        depth: Int = 0
    ) {
        if (node == null) return
        var s = ""
        for (i in 0 until depth) {
            s += "---"
        }
        Log.d(tag, "$s depth: $depth className: " + node.className)
        if (printText && !node.text.isNullOrBlank()) {
            Log.d(tag, "$s depth: $depth text: " + node.text)
        }
        if (printText && !node.contentDescription.isNullOrBlank()) {
            Log.d(tag, "$s depth: $depth desc: " + node.contentDescription)
        }
        for (i in 0 until node.childCount) {
            printNodeClazzTree(node.getChild(i), printText, depth + 1)
        }
    }

}