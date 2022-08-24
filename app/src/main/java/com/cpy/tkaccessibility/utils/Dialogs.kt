package com.cpy.tkaccessibility.utils

import android.annotation.SuppressLint
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.cpy.tkaccessibility.AccessibilityAdapter
import com.cpy.tkaccessibility.AccessibilityMode
import com.cpy.tkaccessibility.R
import com.cpy.tkaccessibility.toast


fun AccessibilityAdapter.commonDialog(
    mode: AccessibilityMode,
    position: Int,
    obtainExtraData: () -> Pair<Boolean, MutableMap<String, Any>>
) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(text = mode.serviceName)
        val idleState = mode.service.idleState()
        val btnText =
            getStringKtx(if (idleState) R.string.accessibility_open else R.string.accessibility_close)
        positiveButton(text = btnText) {
            if (idleState) {
                val data = obtainExtraData.invoke()
                if (!data.first) {
                    toast("参数错误，请检查参数!")
                    return@positiveButton
                }
                mode.service.putExtraData(data.second)
                mode.service.switchIdleState()
            } else {
                mode.service.switchIdleState()
            }
        }
    }
}


@SuppressLint("InflateParams")
fun AccessibilityAdapter.sendGiftConfigDialog(mode: AccessibilityMode, position: Int) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(text = mode.serviceName)
        val btnText =
            getStringKtx(if (mode.service.idleState()) R.string.accessibility_open else R.string.accessibility_close)
//        var edBinding: DialogSendGiftBinding? = null
//        if (mode.service.idleState()) {
//            edBinding = DialogSendGiftBinding.inflate(LayoutInflater.from(context))
//            customView(view = edBinding.root)
//        }
        positiveButton(text = btnText) {
            mode.service.switchIdleState()
//            var targetIndex = edBinding?.roomIndex?.text?.toString()?.toIntOrNull()
//            if (targetIndex != null) {
//                targetIndex += 4
//            }
//            mode.service.getExtrasData()[SendGiftService.TARGET_ROOM_INDEX] = targetIndex
            notifyItemChanged(position)
        }
    }
}