package dev.olog.msc.presentation.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlaylistType

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
    const val CREATE_PLAYLIST = "dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment"
    const val EQUALIZER = "dev.olog.msc.presentation.equalizer.EqualizerFragment"

    const val SETTINGS = "dev.olog.msc.presentation.preferences.settings.SettingsFragmentWrapper"

    const val ARGUMENTS_MEDIA_ID = "argument.media_id"
    const val ARGUMENTS_MEDIA_ID_CATEGORY = "argument.media_id_category"
    const val ARGUMENTS_PLAYLIST_TYPE = "argument.playlist_type"

    private fun instantiate(fragmentManager: FragmentManager, className: String): Fragment {
        val factory = fragmentManager.fragmentFactory
        return factory.instantiate(ClassLoader.getSystemClassLoader(), className)
    }

    fun categories(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, CATEGORIES)
    }

    fun categoriesPodcast(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, CATEGORIES_PODCAST)
    }

    fun search(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, SEARCH)
    }

    fun playingQueue(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, PLAYING_QUEUE)
    }

    fun detail(activity: FragmentActivity, mediaId: MediaId): Fragment {
        return instantiate(activity.supportFragmentManager, DETAIL).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString()
        )
    }

    fun relatedArtists(activity: FragmentActivity, mediaId: MediaId): Fragment {
        return instantiate(activity.supportFragmentManager, RELATED_ARTISTS).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString()
        )
    }

    fun recentlyAdded(activity: FragmentActivity, mediaId: MediaId): Fragment {
        return instantiate(activity.supportFragmentManager, RECENTLY_ADDED).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString()
        )
    }

    fun tab(fragmentManager: FragmentManager, mediaIdCategory: MediaIdCategory): Fragment {
        return instantiate(fragmentManager, TAB).withArguments(
            ARGUMENTS_MEDIA_ID_CATEGORY to mediaIdCategory.ordinal
        )
    }

    fun folderTree(fragmentManager: FragmentManager): Fragment {
        return instantiate(fragmentManager, TAB_FOLDER_TREE)
    }

    fun settings(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, SETTINGS)
    }

    fun createPlaylist(activity: FragmentActivity, type: PlaylistType): Fragment {
        return instantiate(activity.supportFragmentManager, CREATE_PLAYLIST).withArguments(
            ARGUMENTS_PLAYLIST_TYPE to type.ordinal
        )
    }

    fun equalizer(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, EQUALIZER)
    }

}