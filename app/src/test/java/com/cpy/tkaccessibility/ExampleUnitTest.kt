package com.cpy.tkaccessibility

import com.cpy.tkaccessibility.utils.randInt
import org.junit.Test

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