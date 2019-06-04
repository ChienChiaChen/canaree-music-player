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
    const val SLEEP_TIMER = "dev.olog.msc.presentation.sleeptimer.ScrollHmsPickerDialog"
    const val OFFLINE_LYRICS = "dev.olog.msc.presentation.offlinelyrics.OfflineLyricsFragment"
    const val SETTINGS = "dev.olog.msc.presentation.preferences.settings.SettingsFragmentWrapper"

    // arguments
    const val ARGUMENTS_MEDIA_ID = "argument.media_id"
    const val ARGUMENTS_MEDIA_ID_CATEGORY = "argument.media_id.category"
    const val ARGUMENTS_TITLE = "argument.title"
    const val ARGUMENTS_ARTIST = "argument.artist"
    const val ARGUMENTS_ITEM_COUNT = "argument.item_count"
    const val ARGUMENTS_PLAYLIST_TYPE = "argument.playlist_type"

    // dialogs
    const val SET_RINGTONE = "dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialog"
    const val RENAME = "dev.olog.msc.presentation.dialogs.rename.RenameDialog"
    const val CLEAR_PLAYLIST = "dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialog"
    const val NEW_PLAYLIST = "dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialog"
    const val PLAY_LATER = "dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialog"
    const val PLAY_NEXT = "dev.olog.msc.presentation.dialogs.play.next.PlayNextDialog"
    const val ADD_FAVORITE = "dev.olog.msc.presentation.dialogs.favorite.AddFavoriteDialog"
    const val REMOVE_DUPLICATES = "dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog"
    const val DELETE = "dev.olog.msc.presentation.dialogs.delete.DeleteDialog"

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
            ARGUMENTS_MEDIA_ID_CATEGORY to mediaIdCategory.toString()
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

    fun sleepTimer(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, SLEEP_TIMER)
    }

    fun offlineLyrics(activity: FragmentActivity): Fragment {
        return instantiate(activity.supportFragmentManager, OFFLINE_LYRICS)
    }

    fun setRingtone(activity: FragmentActivity, mediaId: MediaId, title: String, artist: String): Fragment {
        return instantiate(activity.supportFragmentManager, SET_RINGTONE).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title,
            ARGUMENTS_ARTIST to artist
        )
    }

    fun rename(activity: FragmentActivity, mediaId: MediaId, title: String): Fragment {
        return instantiate(activity.supportFragmentManager, RENAME).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title
        )
    }

    fun clearPlaylist(activity: FragmentActivity, mediaId: MediaId, title: String): Fragment {
        return instantiate(activity.supportFragmentManager, CLEAR_PLAYLIST).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title
        )
    }

    fun newPlaylist(activity: FragmentActivity, mediaId: MediaId, title: String, itemCount: Int): Fragment {
        return instantiate(activity.supportFragmentManager, NEW_PLAYLIST).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title,
            ARGUMENTS_ITEM_COUNT to itemCount
        )
    }

    fun playLater(activity: FragmentActivity, mediaId: MediaId, title: String, itemCount: Int): Fragment {
        return instantiate(activity.supportFragmentManager, PLAY_LATER).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title,
            ARGUMENTS_ITEM_COUNT to itemCount
        )
    }

    fun playNext(activity: FragmentActivity, mediaId: MediaId, title: String, itemCount: Int): Fragment {
        return instantiate(activity.supportFragmentManager, PLAY_NEXT).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title,
            ARGUMENTS_ITEM_COUNT to itemCount
        )
    }

    fun addFavorite(activity: FragmentActivity, mediaId: MediaId, title: String): Fragment {
        return instantiate(activity.supportFragmentManager, ADD_FAVORITE).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title
        )
    }

    fun removeDuplicates(activity: FragmentActivity, mediaId: MediaId, title: String): Fragment {
        return instantiate(activity.supportFragmentManager, REMOVE_DUPLICATES).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title
        )
    }

    fun delete(activity: FragmentActivity, mediaId: MediaId, title: String, itemCount: Int): Fragment {
        return instantiate(activity.supportFragmentManager, DELETE).withArguments(
            ARGUMENTS_MEDIA_ID to mediaId.toString(),
            ARGUMENTS_TITLE to title,
            ARGUMENTS_ITEM_COUNT to itemCount
        )
    }

}