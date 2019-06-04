package dev.olog.msc.presentation.tabs

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.presentation.tabs.adapters.TabFragmentAdapter
import dev.olog.msc.presentation.tabs.lookup.*

internal object LayoutManagerFactory {

    private fun createSpanSize(context: Context, category: TabCategory, adapter: TabFragmentAdapter): AbsSpanSizeLookup {
        return when (category) {
            TabCategory.PLAYLISTS,
            TabCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(context)
            TabCategory.ALBUMS,
            TabCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(context, adapter)
            TabCategory.ARTISTS,
            TabCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(context, adapter)
            TabCategory.SONGS, TabCategory.PODCASTS -> SongSpanSizeLookup()
            else -> BaseSpanSizeLookup(context)
        }
    }

    fun get(context: Context, category: TabCategory, adapter: TabFragmentAdapter): GridLayoutManager {
        val spanSizeLookup = createSpanSize(context, category, adapter)
        val layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}