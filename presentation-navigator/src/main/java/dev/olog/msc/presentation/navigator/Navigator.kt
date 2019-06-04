package dev.olog.msc.presentation.navigator

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.shared.interfaces.IPopupFacade
import javax.inject.Inject

class Navigator @Inject constructor(
//    private val editItemDialogFactory: EditItemDialogFactory
) {


    private val popupFacade by lazy {
        // TODO find a better way than reflection
        val mainPopup = Class.forName("dev.olog.msc.presentation.popup.PopupFacade")
        mainPopup.newInstance() as IPopupFacade
    }

    fun toFirstAccess(activity: FragmentActivity) {
        activity.startActivity(Intents.splashActivity(activity))
    }

    fun toDetailFragment(activity: FragmentActivity, mediaId: MediaId) {
//        activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).panelState = COLLAPSED TODO
        val newTag = createBackStackTag(Fragments.DETAIL)
        superCerealTransition(activity, Fragments.detail(activity, mediaId), newTag)
    }

    fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId) {
        val newTag = createBackStackTag(Fragments.RELATED_ARTISTS)
        superCerealTransition(activity, Fragments.relatedArtists(activity, mediaId), newTag)
    }

    fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId) {
        val newTag = createBackStackTag(Fragments.RECENTLY_ADDED)
        superCerealTransition(activity, Fragments.recentlyAdded(activity, mediaId), newTag)
    }

    fun toOfflineLyrics(activity: FragmentActivity) {
        if (!allowed()) {
            return
        }
        activity.fragmentTransaction {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(android.R.id.content, Fragments.offlineLyrics(activity), Fragments.OFFLINE_LYRICS)
            addToBackStack(Fragments.OFFLINE_LYRICS)
        }
    }

    fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId) {
//        if (allowed()) {
//            when {
//                mediaId.isLeaf -> {
//                    editItemDialogFactory.toEditTrack(mediaId) {
//                        val instance = EditTrackFragment.newInstance(mediaId)
//                        instance.show(activity.supportFragmentManager, EditTrackFragment.TAG)
//                    }
//                }
//                mediaId.isAlbum || mediaId.isPodcastAlbum -> {
//                    editItemDialogFactory.toEditAlbum(mediaId) {
//                        val instance = EditAlbumFragment.newInstance(mediaId)
//                        instance.show(activity.supportFragmentManager, EditAlbumFragment.TAG)
//                    }
//                }
//                mediaId.isArtist || mediaId.isPodcastArtist -> {
//                    editItemDialogFactory.toEditArtist(mediaId) {
//                        val instance = EditArtistFragment.newInstance(mediaId)
//                        instance.show(activity.supportFragmentManager, EditArtistFragment.TAG)
//                    }
//                }
//                else -> throw IllegalArgumentException("invalid media id $mediaId")
//            }
//        }
    }

    fun toChooseTracksForPlaylistFragment(activity: FragmentActivity, type: PlaylistType) {
        val newTag = createBackStackTag(Fragments.CREATE_PLAYLIST)
        superCerealTransition(activity, Fragments.createPlaylist(activity, type), newTag)
    }

    fun toDialog(mediaId: MediaId, anchor: View) {
        if (!allowed()) {
            return
        }
        try {
            popupFacade.item(anchor, mediaId.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun toMainPopup(activity: FragmentActivity, anchor: View, category: MediaIdCategory?) {
        if (!allowed()) {
            return
        }
        try {
            popupFacade.main(activity, anchor, category?.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun toSetRingtoneDialog(activity: FragmentActivity, mediaId: MediaId, title: String, artist: String) {
        val tag = Fragments.SET_RINGTONE
        activity.fragmentTransaction {
            add(Fragments.setRingtone(activity, mediaId, title, artist), tag)
            addToBackStack(tag)
        }
    }

    fun toAddToFavoriteDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val tag = Fragments.ADD_FAVORITE
        activity.fragmentTransaction {
            add(Fragments.addFavorite(activity, mediaId, itemTitle), tag)
            addToBackStack(tag)
        }
    }

    fun toPlayLater(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val tag = Fragments.PLAY_LATER
        activity.fragmentTransaction {
            add(Fragments.playLater(activity, mediaId, itemTitle, listSize), tag)
            addToBackStack(tag)
        }
    }

    fun toPlayNext(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val tag = Fragments.PLAY_NEXT
        activity.fragmentTransaction {
            add(Fragments.playNext(activity, mediaId, itemTitle, listSize), tag)
            addToBackStack(tag)
        }
    }

    fun toRenameDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val tag = Fragments.RENAME
        activity.fragmentTransaction {
            add(Fragments.rename(activity, mediaId, itemTitle), tag)
            addToBackStack(tag)
        }
    }

    fun toDeleteDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val tag = Fragments.DELETE
        activity.fragmentTransaction {
            add(Fragments.delete(activity, mediaId, itemTitle, listSize), tag)
            addToBackStack(tag)
        }
    }

    fun toCreatePlaylistDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val tag = Fragments.CREATE_PLAYLIST
        activity.fragmentTransaction {
            add(Fragments.newPlaylist(activity, mediaId, itemTitle, listSize), tag)
            addToBackStack(tag)
        }
    }

    fun toClearPlaylistDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val tag = Fragments.CLEAR_PLAYLIST
        activity.fragmentTransaction {
            add(Fragments.clearPlaylist(activity, mediaId, itemTitle), tag)
            addToBackStack(tag)
        }
    }

    fun toRemoveDuplicatesDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val tag = Fragments.REMOVE_DUPLICATES
        activity.fragmentTransaction {
            add(Fragments.removeDuplicates(activity, mediaId, itemTitle), tag)
            addToBackStack(tag)
        }
    }

}