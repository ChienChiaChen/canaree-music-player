package dev.olog.msc.presentation.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.core.Classes
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.msc.shared.ui.extensions.setGone
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import dev.olog.presentation.base.fragment.BaseFragment
import dev.olog.presentation.base.FloatingWindowHelper
import dev.olog.presentation.base.FragmentTags
import dev.olog.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.presentation.base.extensions.*
import dev.olog.presentation.base.utils.ImeUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject

class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"

        @JvmStatic
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<SearchFragmentViewModel>(viewModelFactory) }

    @Inject
    lateinit var adapter: SearchFragmentAdapter
    @Inject
    lateinit var albumAdapter: SearchFragmentAlbumAdapter
    @Inject
    lateinit var artistAdapter: SearchFragmentArtistAdapter
    @Inject
    lateinit var genreAdapter: SearchFragmentGenreAdapter
    @Inject
    lateinit var playlistAdapter: SearchFragmentPlaylistAdapter
    @Inject
    lateinit var folderAdapter: SearchFragmentFolderAdapter
    @Inject
    lateinit var recycledViewPool: androidx.recyclerview.widget.RecyclerView.RecycledViewPool
    @Inject
    lateinit var navigator: Navigator

    @Inject lateinit var classes: Classes

    private lateinit var layoutManager: LinearLayoutManager
    private var bestMatchDisposable: Disposable? = null

    private var queryDisposable: Disposable? = null

    override fun onDetach() {
        val fragmentManager = activity?.supportFragmentManager
        act.fragmentTransaction {
            fragmentManager?.findFragmentByTag(FragmentTags.DETAIL)?.let { show(it) }
                    ?: fragmentManager!!.findFragmentByTag(FragmentTags.CATEGORIES)?.let { show(it) }
            setReorderingAllowed(true)
        }
        super.onDetach()
    }

    private fun searchForBestMatch(query: String) {
        bestMatchDisposable.unsubscribe()
        bestMatchDisposable = viewModel.getBestMatch(query)
                .subscribe({
                    didYouMean.text = it
                    didYouMeanHeader.toggleVisibility(it.isNotBlank(), true)
                    didYouMean.toggleVisibility(it.isNotBlank(), true)
                }, Throwable::printStackTrace)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setRecycledViewPool(recycledViewPool)
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.searchData.subscribe(viewLifecycleOwner) { (map, query) ->

            didYouMean.setGone()
            didYouMeanHeader.setGone()

            if (query.isNotBlank()) {
                val isEmpty = map.map { it.value }
                        .map { it.isEmpty() }
                        .reduce { all, current -> all && current }
                if (isEmpty) {
                    searchForBestMatch(query)
                }
            }

            val albums = map[SearchFragmentType.ALBUMS]!!.toList()
            val artists = map[SearchFragmentType.ARTISTS]!!.toList()
            val playlists = map[SearchFragmentType.PLAYLISTS]!!.toList()
            val genres = map[SearchFragmentType.GENRES]!!.toList()
            val folders = map[SearchFragmentType.FOLDERS]!!.toList()
            albumAdapter.updateDataSet(albums)
            artistAdapter.updateDataSet(artists)
            playlistAdapter.updateDataSet(playlists)
            genreAdapter.updateDataSet(genres)
            folderAdapter.updateDataSet(folders)
            adapter.updateDataSet(viewModel.adjustDataMap(map))
        }

        RxTextView.afterTextChangeEvents(view.editText)
                .map { it.view().text.isBlank() }
                .asLiveData()
                .subscribe(viewLifecycleOwner) { isEmpty ->
                    view.clear.toggleVisibility(!isEmpty, true)
                }
    }

    override fun onResume() {
        super.onResume()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        clear.setOnClickListener { editText.setText("") }
        keyboard.setOnClickListener { ImeUtils.showIme(editText) }
        didYouMean.setOnClickListener { editText.setText(didYouMean.text.toString()) }

        queryDisposable = RxTextView.afterTextChangeEvents(editText)
                .map { it.editable()!!.toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .subscribe(viewModel::setNewQuery, Throwable::printStackTrace)

        adapter.setAfterDataChanged({
            updateLayoutVisibility(it)
        }, false)

        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        more.setOnClickListener {
            try {
                navigator.toMainPopup(requireActivity(), it, null)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        clear.setOnClickListener(null)
        keyboard.setOnClickListener(null)
        didYouMean.setOnClickListener(null)
        queryDisposable.unsubscribe()
        adapter.setAfterDataChanged(null)
        floatingWindow.setOnClickListener(null)
        more.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!, classes.floatingWindowService())
    }


    private fun updateLayoutVisibility(list: List<*>) {
        val itemCount = list.size
        val isEmpty = itemCount == 0
        val queryLength = editText.text.toString().length
        this.searchImage.toggleVisibility(isEmpty && queryLength < 2, true)
        this.list.toggleVisibility(!isEmpty, true)

        val showEmptyState = isEmpty && queryLength >= 2
        this.emptyStateText.toggleVisibility(showEmptyState, true)
        this.emptyStateImage.toggleVisibility(showEmptyState, true)
        if (showEmptyState) {
            this.emptyStateImage.resumeAnimation()
        } else {
            this.emptyStateImage.progress = 0f
        }
    }

    override fun onStop() {
        super.onStop()
        ImeUtils.hideIme(editText)
        bestMatchDisposable.unsubscribe()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}