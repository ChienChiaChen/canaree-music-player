package dev.olog.msc.presentation.library.tab

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.library.tab.span.size.lookup.*

object LayoutManagerFactory {

    private fun createSpanSize(context: Context, category: MediaIdCategory, adapter: TabFragmentAdapter): AbsSpanSizeLookup {
        return when (category){
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