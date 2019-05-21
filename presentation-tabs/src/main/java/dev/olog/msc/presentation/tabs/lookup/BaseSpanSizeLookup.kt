package dev.olog.msc.presentation.tabs.lookup

import android.content.Context
import dev.olog.msc.shared.extensions.configuration

class BaseSpanSizeLookup(
        context: Context

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        var span = 3

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}