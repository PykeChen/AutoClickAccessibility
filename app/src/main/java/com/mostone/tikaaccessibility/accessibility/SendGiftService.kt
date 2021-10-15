package com.mostone.tikaaccessibility.accessibility

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.mostone.tikaaccessibility.*
import com.mostone.tikaaccessibility.accessibility.base.TikaAccessibilitySubProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class SendGiftService : TikaAccessibilitySubProxy() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        when (event?.className) {
            HomePage -> registerAction(HomePage) {
                clickTopRoomFirst(this)
            }
            RoomPage -> registerAction(RoomPage) {
                clickRoomGiftBtn(this)
            }
            SystemDialogClassName -> {
//                //没有区分各种类型礼物箱的特殊标识符
//                val giftPanel = findViewByID(RoomGiftPanel)
//                if (giftPanel?.contentDescription == "礼物箱") {
//                    registerAction(RoomPage) {
//                        sendGift(this)
//                    }
//                }
            }
        }
    }

    private suspend fun clickTopRoomFirst(coroutineScope: CoroutineScope) {
        delay(500L)
        //首页列表
        val homeRvNode = findViewByID(HomeTabRv)
        //找出推荐列表的第一个
        val topRoomFirst = homeRvNode?.runCatching {
            //防止越界
            getChild(4)
        }
        if (topRoomFirst?.isFailure == true) {
            coroutineScope.cancel()
        }
        performViewClick(topRoomFirst?.getOrNull())
        //等待转场动画结束，页面跳转动画为300ms。
        delay(300L)
    }

    private suspend fun clickRoomGiftBtn(
        coroutineScope: CoroutineScope
    ) {
        delay(300L)
        //开始查当前屏幕礼物箱按钮的位置
        var retryCount = 0
        var giftBoxBtn: AccessibilityNodeInfo? = null
        //考虑到进房间的各种接口延时，这里做个循环,10*300L秒之后还是没有找到礼物箱按钮就结束此次任务
        while (retryCount < 10 && coroutineScope.isActive) {
            giftBoxBtn = findViewByID(RoomGiftBtn)
            if (giftBoxBtn != null) {
                break
            }
            delay(300L)
            retryCount += 1
        }
        if (giftBoxBtn == null) {
            coroutineScope.cancel()
        }
        //打开礼物箱
        performViewClick(giftBoxBtn)
        //等待礼物箱打开
        delay(300L)
        sendGift(coroutineScope)
    }

    private suspend fun sendGift(coroutineScope: CoroutineScope) {
        //获取礼物箱的麦位列表
        val micRv = findViewByID(RoomGiftBoxMicUsersRv)
        //获取房主的头像视图
        val avatarContent = micRv?.getChild(0)
        val avatar = avatarContent?.getChild(0)
        val name = avatarContent?.getChild(1)
        //如果未选中就模拟点击选中
        if (name?.isSelected == false) {
            performViewClick(avatar)
        }
        delay(300L)
        //使用默认选中的礼物发送
        //查找送礼按钮
        val senBtn = findViewByID(RoomGiftSendBtn)
        performViewClick(senBtn)
        delay(200L)
        coroutineScope.cancel()
    }
}
