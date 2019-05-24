package dev.olog.msc.presentation.navigator

import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.presentation.base.extensions.collapse
import dev.olog.msc.presentation.base.extensions.fragmentTransaction
import dev.olog.msc.presentation.base.extensions.hideFragmentsIfExists
import dev.olog.msc.presentation.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.categories.track.CategoriesFragment
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.dialogs.delete.DeleteDialog
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog
import dev.olog.msc.presentation.dialogs.favorite.AddFavoriteDialog
import dev.olog.msc.presentation.dialogs.play.later.PlayLaterDialog
import dev.olog.msc.presentation.dialogs.play.next.PlayNextDialog
import dev.olog.msc.presentation.dialogs.playlist.ClearPlaylistDialog
import dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialog
import dev.olog.msc.presentation.dialogs.rename.RenameDialog
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialog
import dev.olog.msc.presentation.edititem.EditItemDialogFactory
import dev.olog.msc.presentation.edititem.album.EditAlbumFragment
import dev.olog.msc.presentation.edititem.artist.EditArtistFragment
import dev.olog.msc.presentation.edititem.track.EditTrackFragment
import dev.olog.msc.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.popup.PopupMenuFactory
import dev.olog.msc.presentation.popup.main.MainPopupDialog
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.presentation.splash.SplashActivity
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

class NavigatorImpl @Inject internal constructor(
    @ProcessLifecycle lifecycle: Lifecycle,
    private val popupFactory: PopupMenuFactory,
    private val mainPopup: MainPopupDialog,
    private val editItemDialogFactory: EditItemDialogFactory

) : Navigator, DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        editItemDialogFactory.dispose()
    }

    private var lastRequest: Long = -1

    override fun toFirstAccess(activity: FragmentActivity, requestCode: Int) {
        val intent = Intent(activity, SplashActivity::class.java)
        activity.startActivityForResult(intent, requestCode)
    }

    private fun anyFragmentOnUpperFragmentContainer(activity: FragmentActivity): Boolean {
        return activity.supportFragmentManager.fragments
            .any { (it.view?.parent as View?)?.id == R.id.upperFragmentContainer }
    }

    private fun getFragmentOnFragmentContainer(activity: FragmentActivity): androidx.fragment.app.Fragment? {
        return activity.supportFragmentManager.fragments
            .firstOrNull { (it.view?.parent as View?)?.id == R.id.fragmentContainer }
    }

    override fun toLibraryCategories(activity: FragmentActivity, forceRecreate: Boolean) {
        if (anyFragmentOnUpperFragmentContainer(activity)) {
            activity.onBackPressed()
        }

        activity.fragmentTransaction {
            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            hideFragmentsIfExists(
                activity, listOf(
                    SearchFragment.TAG,
                    PlayingQueueFragment.TAG,
                    CategoriesPodcastFragment.TAG
                )
            )
            if (forceRecreate) {
                return@fragmentTransaction replace(
                    R.id.fragmentContainer,
                    CategoriesFragment.newInstance(),
                    CategoriesFragment.TAG
                )
            }
            val fragment = activity.supportFragmentManager.findFragmentByTag(CategoriesFragment.TAG)
            if (fragment == null) {
                replace(R.id.fragmentContainer, CategoriesFragment.newInstance(), CategoriesFragment.TAG)
            } else {
                show(fragment)
            }
        }
    }

    override fun toPodcastCategories(activity: FragmentActivity, forceRecreate: Boolean) {
        if (anyFragmentOnUpperFragmentContainer(activity)) {
            activity.onBackPressed()
        }

        activity.fragmentTransaction {
            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            hideFragmentsIfExists(
                activity, listOf(
                    SearchFragment.TAG,
                    PlayingQueueFragment.TAG,
                    CategoriesFragment.TAG
                )
            )
            if (forceRecreate) {
                return@fragmentTransaction replace(
                    R.id.fragmentContainer,
                    CategoriesPodcastFragment.newInstance(),
                    CategoriesPodcastFragment.TAG
                )
            }
            val fragment = activity.supportFragmentManager.findFragmentByTag(CategoriesPodcastFragment.TAG)
            if (fragment == null) {
                replace(R.id.fragmentContainer, CategoriesPodcastFragment.newInstance(), CategoriesPodcastFragment.TAG)
            } else {
                show(fragment)
            }
        }
    }

    override fun toSearchFragment(activity: FragmentActivity) {
        if (anyFragmentOnUpperFragmentContainer(activity)) {
            activity.onBackPressed()
        }

        activity.fragmentTransaction {
            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            hideFragmentsIfExists(
                activity, listOf(
                    CategoriesPodcastFragment.TAG,
                    PlayingQueueFragment.TAG,
                    CategoriesFragment.TAG
                )
            )
            val fragment = activity.supportFragmentManager.findFragmentByTag(SearchFragment.TAG)
            if (fragment == null) {
                replace(R.id.fragmentContainer, SearchFragment.newInstance(), SearchFragment.TAG)
            } else {
                show(fragment)
            }
        }
    }

    override fun toPlayingQueueFragment(activity: FragmentActivity) {
        if (anyFragmentOnUpperFragmentContainer(activity)) {
            activity.onBackPressed()
        }

        activity.fragmentTransaction {
            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            hideFragmentsIfExists(
                activity, listOf(
                    CategoriesPodcastFragment.TAG,
                    SearchFragment.TAG,
                    CategoriesFragment.TAG
                )
            )
            val fragment = activity.supportFragmentManager.findFragmentByTag(PlayingQueueFragment.TAG)
            if (fragment == null) {
                replace(R.id.fragmentContainer, PlayingQueueFragment.newInstance(), PlayingQueueFragment.TAG)
            } else {
                show(fragment)
            }
        }
    }

    override fun toDetailFragment(activity: FragmentActivity, mediaId: MediaId) {

        if (allowed()) {
            activity.findViewById<SlidingUpPanelLayout>(R.id.slidingPanel).collapse()

            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(R.id.upperFragmentContainer, DetailFragment.newInstance(mediaId), DetailFragment.TAG)
                addToBackStack(DetailFragment.TAG)
            }
        }
    }

    override fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(
                    R.id.upperFragmentContainer,
                    RelatedArtistFragment.newInstance(mediaId),
                    RelatedArtistFragment.TAG
                )
                addToBackStack(RelatedArtistFragment.TAG)
            }
        }
    }

    override fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(
                    R.id.upperFragmentContainer,
                    RecentlyAddedFragment.newInstance(mediaId),
                    RecentlyAddedFragment.TAG
                )
                addToBackStack(RecentlyAddedFragment.TAG)
            }
        }
    }

    override fun toOfflineLyrics(activity: FragmentActivity) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(
                    android.R.id.content, OfflineLyricsFragment.newInstance(),
                    OfflineLyricsFragment.TAG
                )
                addToBackStack(OfflineLyricsFragment.TAG)
            }
        }
    }

    override fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId) {
        if (allowed()) {
            when {
                mediaId.isLeaf -> {
                    editItemDialogFactory.toEditTrack(mediaId) {
                        val instance = EditTrackFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditTrackFragment.TAG)
                    }
                }
                mediaId.isAlbum || mediaId.isPodcastAlbum -> {
                    editItemDialogFactory.toEditAlbum(mediaId) {
                        val instance = EditAlbumFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditAlbumFragment.TAG)
                    }
                }
                mediaId.isArtist || mediaId.isPodcastArtist -> {
                    editItemDialogFactory.toEditArtist(mediaId) {
                        val instance = EditArtistFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditArtistFragment.TAG)
                    }
                }
                else -> throw IllegalArgumentException("invalid media id $mediaId")
            }
        }
    }

    override fun toChooseTracksForPlaylistFragment(activity: FragmentActivity, type: PlaylistType) {
        if (allowed()) {
            activity.fragmentTransaction {
                setReorderingAllowed(true)
                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                getFragmentOnFragmentContainer(activity)?.let { hide(it) }
                replace(
                    R.id.upperFragmentContainer,
                    dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment.newInstance(type),
                    dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment.TAG
                )
                addToBackStack(dev.olog.msc.presentation.create.playlist.CreatePlaylistFragment.TAG)
            }
        }
    }

    override fun toDialog(mediaId: MediaId, anchor: View) {
        if (allowed()) {
            popupFactory.show(anchor, mediaId)
        }
    }

    override fun toMainPopup(activity: FragmentActivity, anchor: View, category: MediaIdCategory?) {
        mainPopup.show(activity, anchor, category)
    }

    private fun allowed(): Boolean {
        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
        lastRequest = System.currentTimeMillis()
        return allowed
    }

    override fun toSetRingtoneDialog(activity: FragmentActivity, mediaId: MediaId, title: String, artist: String) {
        val fragment = SetRingtoneDialog.newInstance(mediaId, title, artist)
        fragment.show(activity.supportFragmentManager, SetRingtoneDialog.TAG)
    }

    override fun toAddToFavoriteDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val fragment = AddFavoriteDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, AddFavoriteDialog.TAG)
    }

    override fun toPlayLater(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = PlayLaterDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayLaterDialog.TAG)
    }

    override fun toPlayNext(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = PlayNextDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, PlayNextDialog.TAG)
    }

    override fun toRenameDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val fragment = RenameDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RenameDialog.TAG)
    }

    override fun toDeleteDialog(activity: FragmentActivity, mediaId: MediaId, listSize: Int, itemTitle: String) {
        val fragment = DeleteDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, DeleteDialog.TAG)
    }

    override fun toCreatePlaylistDialog(
        activity: FragmentActivity,
        mediaId: MediaId,
        listSize: Int,
        itemTitle: String
    ) {
        val fragment = NewPlaylistDialog.newInstance(mediaId, listSize, itemTitle)
        fragment.show(activity.supportFragmentManager, NewPlaylistDialog.TAG)
    }

    override fun toClearPlaylistDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val fragment = ClearPlaylistDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, ClearPlaylistDialog.TAG)
    }

    override fun toRemoveDuplicatesDialog(activity: FragmentActivity, mediaId: MediaId, itemTitle: String) {
        val fragment = RemoveDuplicatesDialog.newInstance(mediaId, itemTitle)
        fragment.show(activity.supportFragmentManager, RemoveDuplicatesDialog.TAG)
    }

}
