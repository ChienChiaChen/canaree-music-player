package dev.olog.msc.presentation.navigator

import android.content.Context
import androidx.fragment.app.Fragment
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory

object Fragments {

    const val CATEGORIES = "dev.olog.msc.presentation.categories.track.CategoriesFragment"
    const val CATEGORIES_PODCAST = "dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment"
    const val SEARCH = "dev.olog.msc.presentation.search.SearchFragment"
    const val PLAYING_QUEUE = "dev.olog.msc.presentation.playing.queue.PlayingQueueFragment"

    const val TAB = "dev.olog.msc.presentation.tabs.TabFragment"
    const val TAB_FOLDER_TREE = "dev.olog.msc.presentation.tabs.foldertree.FolderTreeFragment"
    const val DETAIL = "dev.olog.msc.presentation.detail.DetailFragment"
    const val RELATED_ARTISTS = "dev.olog.msc.presentation.related.artists.RelatedArtistFragment"
    const val RECENTLY_ADDED = "dev.olog.msc.presentation.recently.added.RecentlyAddedFragment"

    const val SETTINGS = "dev.olog.msc.presentation.preferences.settings.SettingsFragmentWrapper"

    const val ARGUMENTS_MEDIA_ID = "argument.media_id"
    const val ARGUMENTS_MEDIA_ID_CATEGORY = "argument.media_id_category"

    fun categories(context: Context): Fragment {
        return Fragment.instantiate(context, CATEGORIES)
    }

    fun categoriesPodcast(context: Context): Fragment {
        return Fragment.instantiate(context, CATEGORIES_PODCAST)
    }

    fun search(context: Context): Fragment {
        return Fragment.instantiate(context, SEARCH)
    }

    fun playingQueue(context: Context): Fragment {
        return Fragment.instantiate(context, PLAYING_QUEUE)
    }

    fun detail(context: Context, mediaId: MediaId): Fragment {
        return Fragment.instantiate(context, DETAIL).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString()
        )
    }

    fun relatedArtists(context: Context, mediaId: MediaId): Fragment {
        return Fragment.instantiate(context, RELATED_ARTISTS).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString()
        )
    }

    fun recentlyAdded(context: Context, mediaId: MediaId): Fragment {
        return Fragment.instantiate(context, RECENTLY_ADDED).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString()
        )
    }

    fun tab(context: Context, mediaIdCategory: MediaIdCategory): Fragment {
        return Fragment.instantiate(context, TAB).withArguments(
            ARGUMENTS_MEDIA_ID_CATEGORY to mediaIdCategory.ordinal
        )
    }

    fun folderTree(context: Context): Fragment {
        return Fragment.instantiate(context, TAB_FOLDER_TREE)
    }

    fun settings(context: Context): Fragment {
        return Fragment.instantiate(context, SETTINGS)
    }

}