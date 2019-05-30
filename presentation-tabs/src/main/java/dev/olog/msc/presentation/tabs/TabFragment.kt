package dev.olog.msc.presentation.tabs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.core.entity.sort.SortType
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.extensions.parentViewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.list.BasePagedAdapter
import dev.olog.msc.presentation.base.list.SetupNestedList
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.media.MediaProvider
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.tabs.adapters.TabFragmentAdapter
import dev.olog.msc.presentation.tabs.adapters.TabFragmentNestedAdapter
import dev.olog.msc.presentation.tabs.di.inject
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.ui.extensions.subscribe
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import dev.olog.msc.shared.utils.TextUtils
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.fragment_tab.view.*
import javax.inject.Inject

class TabFragment : BaseFragment(), SetupNestedList {

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val lastAlbumsAdapter by lazyFast { TabFragmentNestedAdapter(navigator) }
    private val lastArtistsAdapter by lazyFast { TabFragmentNestedAdapter(navigator) }
    private val newAlbumsAdapter by lazyFast { TabFragmentNestedAdapter(navigator) }
    private val newArtistsAdapter by lazyFast { TabFragmentNestedAdapter(navigator) }

    private val viewModel by lazyFast { parentViewModelProvider<TabFragmentViewModel>(viewModelFactory) }

    private val category by lazyFast {
        val ordinalCategory = arguments!!.getInt(Fragments.ARGUMENTS_MEDIA_ID_CATEGORY)
        MediaIdCategory.values()[ordinalCategory]
    }

    private val adapter by lazyFast { TabFragmentAdapter(category, navigator, viewModel, act as MediaProvider, this) }

    private fun handleEmptyStateVisibility(isEmpty: Boolean) {
        emptyStateText.toggleVisibility(isEmpty, true)
        if (isEmpty) {
            if (isPodcastFragment()) {
                val emptyText = resources.getStringArray(R.array.tab_empty_podcast)
                emptyStateText.text = emptyText[category.ordinal - 6]
            } else {
                val emptyText = resources.getStringArray(R.array.tab_empty_state)
                emptyStateText.text = emptyText[category.ordinal]
            }
        }
    }

    private fun isPodcastFragment(): Boolean {
        return category == MediaIdCategory.PODCASTS || category == MediaIdCategory.PODCASTS_PLAYLIST ||
                category == MediaIdCategory.PODCASTS_ALBUMS || category == MediaIdCategory.PODCASTS_ARTISTS
    }

    override fun injectComponent() {
        inject()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val gridLayoutManager = LayoutManagerFactory.get(act, category, adapter)
        view.list.layoutManager = gridLayoutManager
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        applyMarginToList(view)

        val scrollableLayoutId = when (category) {
            MediaIdCategory.SONGS -> R.layout.item_tab_song
            MediaIdCategory.PODCASTS -> R.layout.item_tab_podcast
            MediaIdCategory.ARTISTS -> R.layout.item_tab_artist
            else -> R.layout.item_tab_album
        }
        view.sidebar.scrollableLayoutId = scrollableLayoutId

        view.fab.toggleVisibility(
            category == MediaIdCategory.PLAYLISTS ||
                    category == MediaIdCategory.PODCASTS_PLAYLIST, true
        )

        viewModel.observeData(category)
            .subscribe(viewLifecycleOwner) { list ->
                handleEmptyStateVisibility(list.isEmpty())
                adapter.submitList(list)
//                sidebar.onDataChanged(list) TODO
            }

        when (category) {
            MediaIdCategory.ALBUMS -> {
                viewModel.observeData(MediaIdCategory.LAST_PLAYED_ALBUMS)
                    .subscribe(viewLifecycleOwner) { lastAlbumsAdapter.submitList(it) }
                viewModel.observeData(MediaIdCategory.RECENTLY_ADDED_ALBUMS)
                    .subscribe(viewLifecycleOwner) { newAlbumsAdapter.submitList(it) }
            }
            MediaIdCategory.ARTISTS -> {
                viewModel.observeData(MediaIdCategory.LAST_PLAYED_ARTISTS)
                    .subscribe(viewLifecycleOwner) { lastArtistsAdapter.submitList(it) }
                viewModel.observeData(MediaIdCategory.RECENTLY_ADDED_ARTISTS)
                    .subscribe(viewLifecycleOwner) { newArtistsAdapter.submitList(it) }
            }
            MediaIdCategory.PODCASTS_ALBUMS -> {
                viewModel.observeData(MediaIdCategory.LAST_PLAYED_PODCAST_ALBUMS)
                    .subscribe(viewLifecycleOwner) { lastAlbumsAdapter.submitList(it) }
                viewModel.observeData(MediaIdCategory.RECENTLY_ADDED_PODCAST_ALBUMS)
                    .subscribe(viewLifecycleOwner) { newAlbumsAdapter.submitList(it) }
            }
            MediaIdCategory.PODCASTS_ARTISTS -> {
                viewModel.observeData(MediaIdCategory.LAST_PLAYED_PODCAST_ARTISTS)
                    .subscribe(viewLifecycleOwner) { lastArtistsAdapter.submitList(it) }
                viewModel.observeData(MediaIdCategory.RECENTLY_ADDED_PODCAST_ARTISTS)
                    .subscribe(viewLifecycleOwner) { newArtistsAdapter.submitList(it) }
            }
            else -> {/*making lint happy*/
            }
        }
    }

    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId){
            R.layout.item_tab_last_played_album_horizontal_list -> setupHorizontalList(recyclerView, lastAlbumsAdapter)
            R.layout.item_tab_last_played_artist_horizontal_list -> setupHorizontalList(recyclerView, lastArtistsAdapter)
            R.layout.item_tab_new_album_horizontal_list -> setupHorizontalList(recyclerView, newAlbumsAdapter)
            R.layout.item_tab_new_artist_horizontal_list -> setupHorizontalList(recyclerView, newArtistsAdapter)
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: BasePagedAdapter<*>) {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            list.context,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
//        sidebar.setListener(letterTouchListener)
        fab?.setOnClickListener {
            if (category == MediaIdCategory.PLAYLISTS) {
                navigator.toChooseTracksForPlaylistFragment(requireActivity(), PlaylistType.TRACK)
            } else {
                navigator.toChooseTracksForPlaylistFragment(requireActivity(), PlaylistType.PODCAST)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        fab?.setOnClickListener(null)
    }

    private fun applyMarginToList(view: View) {
        if (category == MediaIdCategory.SONGS || category == MediaIdCategory.PODCASTS) {
            // start/end margin is set in item
            view.list.setPadding(
                view.list.paddingLeft, view.list.paddingTop,
                view.list.paddingRight, ctx.dimen(R.dimen.tab_margin_bottom)
            )
        } else {
            view.list.setPadding(
                ctx.dimen(R.dimen.tab_margin_start), ctx.dimen(R.dimen.tab_margin_top),
                ctx.dimen(R.dimen.tab_margin_end), ctx.dimen(R.dimen.tab_margin_bottom)
            )
        }
    }

//    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter ->
//        list.stopScroll()
//
//        val scrollableItem = sidebar.scrollableLayoutId
//
//        val position = when (letter) {
//            TextUtils.MIDDLE_DOT -> -1
//            "#" -> adapter.indexOf {
//                if (it.type != scrollableItem) {
//                    false
//                } else {
//                    val sorting = getCurrentSorting(it)
//                    if (sorting.isBlank()) false
//                    else sorting[0].toUpperCase().toString().isDigitsOnly()
//                }
//            }
//            "?" -> adapter.indexOf {
//                if (it.type != scrollableItem) {
//                    false
//                } else {
//                    val sorting = getCurrentSorting(it)
//                    if (sorting.isBlank()) false
//                    else sorting[0].toUpperCase().toString() > "Z"
//                }
//            }
//            else -> adapter.indexOf {
//                if (it.type != scrollableItem) {
//                    false
//                } else {
//                    val sorting = getCurrentSorting(it)
//                    if (sorting.isBlank()) false
//                    else sorting[0].toUpperCase().toString() == letter
//                }
//            }
//        }
//        if (position != -1) {
//            val layoutManager = list.layoutManager as androidx.recyclerview.widget.GridLayoutManager
//            layoutManager.scrollToPositionWithOffset(position, 0)
//        }
//    }

    private fun getCurrentSorting(item: DisplayableItem): String {
        return when (category) {
            MediaIdCategory.SONGS -> {
                val sortOrder = viewModel.getAllTracksSortOrder()
                when (sortOrder.type) {
                    SortType.ARTIST -> item.subtitle!!
                    SortType.ALBUM -> item.subtitle!!.substring(item.subtitle!!.indexOf(TextUtils.MIDDLE_DOT) + 1).trim()
                    else -> item.title
                }
            }
            MediaIdCategory.ALBUMS -> {
                val sortOrder = viewModel.getAllAlbumsSortOrder()
                when (sortOrder.type) {
                    SortType.TITLE -> item.title
                    else -> item.subtitle!!
                }
            }
            else -> item.title
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_tab
}