package dev.olog.msc.presentation.tabs

import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.tabs.adapters.TabFragmentLastPlayedAlbumsAdapter
import dev.olog.msc.presentation.tabs.adapters.TabFragmentLastPlayedArtistsAdapter
import dev.olog.msc.presentation.tabs.adapters.TabFragmentNewAlbumsAdapter
import dev.olog.msc.presentation.tabs.adapters.TabFragmentNewArtistsAdapter

internal object TabHorizontalAdapters {

    fun getLastPlayedAlbums(fragment: TabFragment): TabFragmentLastPlayedAlbumsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ALBUMS || category == MediaIdCategory.PODCASTS_ALBUMS){
            return TabFragmentLastPlayedAlbumsAdapter(fragment.navigator)
        }
        return null
    }

    fun getLastPlayedArtists(fragment: TabFragment): TabFragmentLastPlayedArtistsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ARTISTS || category == MediaIdCategory.PODCASTS_ARTISTS){
            return TabFragmentLastPlayedArtistsAdapter(fragment.navigator)
        }
        return null
    }

    fun getNewAlbums(fragment: TabFragment): TabFragmentNewAlbumsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ALBUMS || category == MediaIdCategory.PODCASTS_ALBUMS){
            return TabFragmentNewAlbumsAdapter(fragment.navigator)
        }
        return null
    }

    fun getNewArtists(fragment: TabFragment): TabFragmentNewArtistsAdapter? {
        val category = fragment.category
        if (category == MediaIdCategory.ARTISTS || category == MediaIdCategory.PODCASTS_ARTISTS){
            return TabFragmentNewArtistsAdapter(fragment.navigator)
        }
        return null
    }

}