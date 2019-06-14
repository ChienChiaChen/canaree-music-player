package dev.olog.msc.data

import dev.olog.msc.data.utils.TextUtils
import org.junit.Assert
import org.junit.Test

class StringPatternsTest {

    @Test
    fun `add space between dash`(){
        val string = "3005-childish gambino"

        val withNoDash = TextUtils.addSpacesToDash(string)

        Assert.assertEquals("3005 - childish gambino", withNoDash)
    }

    @Test
    fun `add space between with left space dash`(){
        val string = "3005 -childish gambino"

        val withNoDash = TextUtils.addSpacesToDash(string)

        Assert.assertEquals("3005 - childish gambino", withNoDash)
    }

    @Test
    fun `add space between with right space dash`(){
        val string = "3005- childish gambino"

        val withNoDash = TextUtils.addSpacesToDash(string)

        Assert.assertEquals("3005 - childish gambino", withNoDash)
    }

}