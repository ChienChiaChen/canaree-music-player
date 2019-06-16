package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.utils.k.extension.configuration


class PlaylistSpanSizeLookup(
        context: Context

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        when (position){
            0, 4 -> return spanCount
        }

        var span = 3

        if (isTablet) {
            span++
        }

        return spanCount / span
    }

}