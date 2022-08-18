package com.mostone.tikaaccessibility

import com.mostone.tikaaccessibility.utils.randInt
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println("random 0-4:${randInt(0, 4)}")
    }
}