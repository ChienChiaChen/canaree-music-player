package dev.olog.msc.presentation.tabs.lookup

import android.content.Context
import dev.olog.msc.presentation.base.list.BasePagedAdapter
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.tabs.R
import dev.olog.msc.shared.extensions.configuration

class ArtistSpanSizeLookup(
        context: Context,
        private val adapter: BasePagedAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.getItemViewType(position)
        when (itemType) {
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> return spanCount
        }

        var span = 3

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}