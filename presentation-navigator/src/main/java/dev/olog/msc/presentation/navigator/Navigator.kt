package dev.olog.msc.presentation.navigator

import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlaylistType

interface Navigator {

    fun toFirstAccess(activity: FragmentActivity, requestCode: Int)

    fun toLibraryCategories(activity: FragmentActivity, forceRecreate: Boolean)
    fun toPodcastCategories(activity: FragmentActivity, forceRecreate: Boolean)

    fun toDetailFragment(activity: FragmentActivity, mediaId: MediaId)

    fun toSearchFragment(activity: FragmentActivity)

    fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId)

    fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId)

    fun toPlayingQueueFragment(activity: FragmentActivity)

    fun toChooseTracksForPlaylistFragment(activity: FragmentActivity, type: PlaylistType)

    fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId)

    fun toOfflineLyrics(activity: FragmentActivity)

    fun toDialog(mediaId: MediaId, anchor: View)

    fun toMainPopup(activity: FragmentActivity, anchor: View, category: MediaIdCategory?)

    fun toSetRingtoneDialog(activity: FragmentActivity, mediaId: MediaId, title: String, artist: String)

    fun toCreatePlaylistDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toAddToFavoriteDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toPlayLater(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toPlayNext(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRenameDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String)

    fun toClearPlaylistDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String)

    fun toDeleteDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String)

    fun toRemoveDuplicatesDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String)

}