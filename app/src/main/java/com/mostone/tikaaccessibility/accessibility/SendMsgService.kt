package com.mostone.tikaaccessibility.accessibility

import android.view.accessibility.AccessibilityEvent
import com.mostone.tikaaccessibility.HomeChatPage
import com.mostone.tikaaccessibility.accessibility.base.TikaAccessibilitySubProxy
import kotlinx.coroutines.*

class SendMsgService : TikaAccessibilitySubProxy() {
    private val mInputId = "com.mostone.tika:id/et_input"
    private val mSendId = "com.mostone.tika:id/btn_send"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        runInViewTarget(event, HomeChatPage) {
            val inputView = findViewByID(mInputId)
            val sendText = "无障碍服务测试"
            //发送100条消息
            if (inputView != null) {
                for (i in 0 until 100) {
                    if (!isActive) break
                    delay(500L)
                    inputText(inputView, sendText)
                    clickTextViewByID(mSendId)
                }
            }
        }
    }

}