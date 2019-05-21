package dev.olog.msc.presentation.tabs

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.tabs.adapters.TabFragmentAdapter
import dev.olog.msc.presentation.tabs.lookup.*

internal object LayoutManagerFactory {

    private fun createSpanSize(context: Context, category: MediaIdCategory, adapter: TabFragmentAdapter): AbsSpanSizeLookup {
        return when (category) {
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> PlaylistSpanSizeLookup(context)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> AlbumSpanSizeLookup(context, adapter)
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> ArtistSpanSizeLookup(context, adapter)
            MediaIdCategory.SONGS, MediaIdCategory.PODCASTS -> SongSpanSizeLookup()
            else -> BaseSpanSizeLookup(context)
        }
    }

    fun get(context: Context, category: MediaIdCategory, adapter: TabFragmentAdapter): GridLayoutManager {
        val spanSizeLookup = createSpanSize(context, category, adapter)
        val layoutManager = GridLayoutManager(context, spanSizeLookup.getSpanSize())
        layoutManager.spanSizeLookup = spanSizeLookup
        return layoutManager
    }

}