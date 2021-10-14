package com.mostone.tikaaccessibility.utils

import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.mostone.tikaaccessibility.AccessibilityAdapter
import com.mostone.tikaaccessibility.AccessibilityMode
import com.mostone.tikaaccessibility.R


fun AccessibilityAdapter.commonDialog(mode: AccessibilityMode, position: Int) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(text = mode.serviceName)
        val btnText =
            getStringKtx(if (mode.service.idleState()) R.string.accessibility_open else R.string.accessibility_close)
        positiveButton(text = btnText) {
            mode.service.switchIdleState()
            notifyItemChanged(position)
        }
    }
}