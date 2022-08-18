package com.cpy.tkaccessibility.float

import ItemViewTouchListener
import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import com.cpy.tkaccessibility.R

/**
 * FloatWindowManager
 * @author cpy
 * Created on 2022-08-18 16:23
 */
class FloatWindowManager {


    var floatRootView: View? = null//悬浮窗View
    var isReceptionShow = false

    companion object {


        /**
         * 关闭所有悬浮窗
         */
        fun closeAllSuspendWindow(activity: Activity, floatRootView: View) {
            if (!Utils.isNull(floatRootView)) {
                if (!Utils.isNull(floatRootView.windowToken)) {
                    if (!Utils.isNull(activity.windowManager)) {
                        activity.windowManager?.removeView(floatRootView)
                    }
                }
            }
            ViewModelMain.isShowSuspendWindow.postValue(false)
            ViewModelMain.isShowWindow.postValue(false)
        }

        /**
         * 应用界面内显示悬浮球
         */
        @SuppressLint("ClickableViewAccessibility")
        fun showCurrentWindow(activity: Activity): View {
            val layoutParam = WindowManager.LayoutParams().apply {
                //设置大小 自适应
                width = WRAP_CONTENT
                height = WRAP_CONTENT
                flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }
            // 新建悬浮窗控件
            val floatRootView =
                LayoutInflater.from(activity).inflate(R.layout.activity_float_item, null)
            //设置拖动事件
            floatRootView?.setOnTouchListener(
                ItemViewTouchListener(
                    layoutParam,
                    activity.windowManager
                )
            )
            // 将悬浮窗控件添加到WindowManager
            activity.windowManager.addView(floatRootView, layoutParam)
            return floatRootView
        }

    }


}