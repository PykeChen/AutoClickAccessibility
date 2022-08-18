package com.cpy.tkaccessibility.utils

import android.util.Log
import android.widget.EditText
import java.util.*


private val mRandom by lazy(LazyThreadSafetyMode.NONE) {
    Random()
}

fun randInt(start: Int, end: Int): Int {
    return mRandom.nextInt((end - start) + 1) + start
}


fun EditText.saveTxt2Sp(key: String) {
    this.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            SPUtils.getInstance().put(key, text.toString())
        }
    }
}