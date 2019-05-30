package dev.olog.msc.presentation.navigator

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.shared.interfaces.MainPopup
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

class Navigator @Inject constructor(
//    private val popupFactory: PopupMenuFactory,
    private val popupNavigator: IPopupNavigator,
    private val sortGateway: SortPreferencesGateway
//    private val editItemDialogFactory: EditItemDialogFactory
) {

    private var lastRequest: Long = -1

    private val mainPopup by lazy {
        // TODO find a better way than reflection
        val mainPopup = Class.forName("dev.olog.msc.presentation.popup.main.MainPopupDialog")
        val contructor = mainPopup.getConstructor(IPopupNavigator::class.java, SortPreferencesGateway::class.java)
        contructor.newInstance(popupNavigator, sortGateway) as MainPopup
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

    // fragment tag, last added
    private var backStackCount = mutableMapOf<String, Int>()

    fun toFirstAccess(activity: FragmentActivity) {
        activity.startActivity(Intents.splashActivity(activity))
    }

    private fun createBackstackTag(fragmentTag: String): String {
        // get last
        val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
        // update
        backStackCount[fragmentTag] = counter
        // creates new
        return "$fragmentTag$counter"
    }

    private fun getFragmentOnFragmentContainer(activity: FragmentActivity): androidx.fragment.app.Fragment? {
        return activity.supportFragmentManager.fragments
            .firstOrNull { (it.view?.parent as View?)?.id == R.id.fragmentContainer }
    }

    fun toDetailFragment(activity: FragmentActivity, mediaId: MediaId) {

        if (allowed()) {
            // TODO useful only when navigating from player fragment expanded
            activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).panelState = COLLAPSED

            val newTag = createBackstackTag(Fragments.DETAIL)

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(R.id.upperFragmentContainer, Fragments.detail(activity, mediaId), newTag)
                addToBackStack(newTag)
            }
        }
    }

    fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId) {
        if (allowed()) {

            val newTag = createBackstackTag(Fragments.RELATED_ARTISTS)

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(
                    R.id.upperFragmentContainer,
                    Fragments.relatedArtists(activity, mediaId),
                    newTag
                )
                addToBackStack(newTag)
            }
        }
    }

    fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId) {
        if (allowed()) {

            val newTag = createBackstackTag(Fragments.RECENTLY_ADDED)

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(
                    R.id.upperFragmentContainer,
                    Fragments.recentlyAdded(activity, mediaId),
                    newTag
                )
                addToBackStack(newTag)
            }
        }
    }

    fun toOfflineLyrics(activity: FragmentActivity) {
//        if (allowed()) {
//            activity.fragmentTransaction {
//                setReorderingAllowed(true)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                add(
//                    android.R.id.content, OfflineLyricsFragment.newInstance(),
//                    OfflineLyricsFragment.TAG
//                )
//                addToBackStack(OfflineLyricsFragment.TAG)
//            }
//        }
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
//        if (allowed()) {
//            activity.fragmentTransaction {
//                setReorderingAllowed(true)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
//                replace(
//                    R.id.upperFragmentContainer,
//                    dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment.newInstance(type),
//                    dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment.TAG
//                )
//                addToBackStack(dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment.TAG)
//            }
//        }
    }

    fun toDialog(mediaId: MediaId, anchor: View) {
//        if (allowed()) {
//            popupFactory.show(anchor, mediaId)
//        }
    }

    fun toMainPopup(activity: FragmentActivity, anchor: View, category: MediaIdCategory?) {
        try {
            mainPopup.show(activity, anchor, category?.ordinal)
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    fun toSetRingtoneDialog(activity: FragmentActivity, mediaId: MediaId, title: String, artist: String) {
//        val fragment = SetRingtoneDialog.newInstance(mediaId, title, artist)
//        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    fun toAddToFavoriteDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
//        val fragment = AddFavoriteDialog.newInstance(mediaId, itemTitle)
//        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    fun toPlayLater(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
//        val fragment = PlayLaterDialog.newInstance(mediaId, listSize, itemTitle)
//        fragment.show(activity.supportFragmentManager, PlayLaterDialog.TAG)
    }

    fun toPlayNext(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
//        val fragment = PlayNextDialog.newInstance(mediaId, listSize, itemTitle)
//        fragment.show(activity.supportFragmentManager, PlayNextDialog.TAG)
    }

    fun toRenameDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
//        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
//        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }

    fun toDeleteDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
//        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
//        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
//        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
//        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    fun toClearPlaylistDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
//        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
//        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }

    fun toRemoveDuplicatesDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
//        val fragment = RemoveDuplicatesDialog.newInstance(mediaId, itemTitle)
//        fragment.show(activity.supportFragmentManager, RemoveDuplicatesDialog.TAG)
    }

}