package com.mostone.tikaaccessibility.utils

import java.util.*


private val mRandom by lazy(LazyThreadSafetyMode.NONE) {
    Random()
}

fun randInt(start: Int, end: Int): Int {
    return mRandom.nextInt((end - start) + 1) + start
}