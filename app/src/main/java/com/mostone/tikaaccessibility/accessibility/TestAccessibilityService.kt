package com.mostone.tikaaccessibility.accessibility

import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestAccessibilityService : BaseAccessibilityService() {

    private val mInputId = "com.mostone.tika:id/et_input"
    private val mSendId = "com.mostone.tika:id/btn_send"

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)

        val inputView = findViewByID(mInputId)
        val sendText = "无障碍服务测试"
        scope.launch {
            //发送100条消息
            if (inputView != null) {
                repeat(100) {
                    delay(500L)
                    inputText(inputView, sendText)
                    clickTextViewByID(mSendId)
                }
            }
        }

    }

    companion object {
        const val TAG = "TestAccessibility"
    }
}