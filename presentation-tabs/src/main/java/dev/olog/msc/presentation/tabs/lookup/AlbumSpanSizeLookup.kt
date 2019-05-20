package dev.olog.msc.presentation.tabs.lookup

import android.content.Context
import dev.olog.msc.presentation.base.adapter.BasePagedAdapter
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.tabs.R
import dev.olog.msc.shared.extensions.configuration
import dev.olog.msc.shared.extensions.isPortrait

class AlbumSpanSizeLookup(
    context: Context,
    private val adapter: BasePagedAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val isPortrait = context.isPortrait
    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.getItemViewType(position)
        when (itemType) {
            R.layout.item_tab_header,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list -> return spanCount
        }
//
        if (isTablet) {
            val span = if (isPortrait) 4 else 5
            return spanCount / span
        }

        return if (isPortrait) spanCount / 2 else spanCount / 4
    }

}