package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.configuration

class AlbumSpanSizeLookup(
        private val context: Context,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        when (itemType){
            R.layout.item_tab_header,
            R.layout.item_tab_new_album_horizontal_list,
            R.layout.item_tab_last_played_album_horizontal_list -> return spanCount
        }
//
        if (isTablet){
            val span = 4
            return spanCount / span
        }

        return spanCount / 2
    }

}