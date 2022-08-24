package com.cpy.tkaccessibility.accessibility

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.cpy.tkaccessibility.*
import com.cpy.tkaccessibility.accessibility.base.TikaAccessibilitySubProxy
import com.cpy.tkaccessibility.utils.TimeUtils
import kotlinx.coroutines.*
import java.util.*


class DiscountFetchService(private val itemPos: Int, private val mListener: IServiceChange) :
    TikaAccessibilitySubProxy() {

    private var mToastDuration = 1000L
    private var mToastCount = 0
    private var mCatchClassName: String? = null
    private var mStartDate: Date? = null
    private var mEndDate: Date? = null
    private var mXPos: Float = 0f
    private var mYPos: Float = 0f
    private var mTapDur: Long = 0

    var mTimerTask: Timer? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (mCatchClassName.isNullOrEmpty() || event == null) {
            return
        }
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (event.className.contains(mCatchClassName!!, ignoreCase = true)) {
                    if (mTimerTask != null) {
                        return
                    }
                    startAutoClickTask()
                } else {
                    stopAutoClickTask()
                }
            }

            else -> {

            }

        }

    }

    private fun stopAutoClickTask() {
        mTimerTask?.let {
            toastIn("停止AutoC服务")
            ViewModelMain.tips.postValue("")
            it.cancel()
            mTimerTask = null
        }

    }

    private fun toastIn(toastV: String) {
        mCoroutineScope.launch {
            toast(toastV)
        }
    }

    private fun startAutoClickTask() {
        val clickFn: () -> Unit = {
            mCoroutineScope.launch {
//                val viewF = findViewByText("领取", clickable = true)
                if (System.currentTimeMillis() > mEndDate!!.time) {
                    stopAutoClickTask()
                    return@launch
                }
                Log.d("Accessibility", "startAutoClickTask: count($mToastCount)")
                if ((mToastCount * mTapDur) % mToastDuration == 0L) {
                    toastIn("执行点击(x=$mXPos, y=$mYPos)")
                }
                mToastCount++
                performXYClick(mXPos, mYPos)
            }
        }
        toastIn("启动任务(${TimeUtils.time2Date(mStartDate!!)})")
        ViewModelMain.tips.postValue(TimeUtils.time2DateSeconds(mStartDate!!))
        mTimerTask = if (mStartDate!!.before(Date())) {
            kotlin.concurrent.timer(
                TAG_NAME,
                daemon = false,
                initialDelay = 0,
                period = mTapDur
            ) {
                clickFn()
            }
        } else {
            kotlin.concurrent.timer(
                TAG_NAME,
                daemon = false,
                startAt = mStartDate!!,
                period = mTapDur
            ) {
                clickFn()
            }
        }

    }


    override fun putExtraData(extraData: MutableMap<String, Any>) {
        super.putExtraData(extraData)
        mCatchClassName = extraData[KEY_CLASS_NAME] as String
        mStartDate = extraData[KEY_START_DATE] as Date
        mEndDate = extraData[KEY_END_DATE] as Date
        mXPos = extraData[KEY_X_POS] as Float
        mYPos = extraData[KEY_Y_POS] as Float
        mTapDur = extraData[KEY_TAP_DUR] as Long
    }


    override fun switchIdleState() {
        super.switchIdleState()
        stopAutoClickTask()
        mCoroutineScope.launch {
            mListener.idleChange(idle = mIdle, itemPos)
        }
    }

    fun obtainStartDate(): Date? {
        return mStartDate
    }

    companion object {
        const val WebViewActivity = "com.pupumall.webview.page.BannerWebViewActivity"
        const val TAG_NAME = "DiscountFetchService"
    }

    interface IServiceChange {
        fun idleChange(idle: Boolean, position: Int)
    }
}
