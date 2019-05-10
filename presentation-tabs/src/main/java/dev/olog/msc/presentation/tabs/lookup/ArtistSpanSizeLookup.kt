package dev.olog.msc.presentation.tabs.lookup

import android.content.Context
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.R
import dev.olog.msc.shared.extensions.configuration

class ArtistSpanSizeLookup(
        context: Context,
        private val isPortrait: Boolean,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        when (itemType){
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> return spanCount
        }

        var span = if (isPortrait) 3 else 4

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}