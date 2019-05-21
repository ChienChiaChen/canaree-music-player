package dev.olog.msc.presentation.search

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.presentation.base.FragmentTags
import dev.olog.msc.presentation.base.adapter.SetupNestedList
import dev.olog.msc.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.base.extensions.*
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.utils.ImeUtils
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.search.adapters.SearchFragmentAdapter
import dev.olog.msc.presentation.search.adapters.SearchFragmentNestedAdapter
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchFragment : BaseFragment(), SetupNestedList {

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

    private lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazyFast { SearchFragmentAdapter(navigator, viewModel, this) }
    private val albumAdapter by lazyFast { SearchFragmentNestedAdapter(navigator, viewModel) }
    private val artistAdapter by lazyFast { SearchFragmentNestedAdapter(navigator, viewModel) }
    private val genreAdapter by lazyFast { SearchFragmentNestedAdapter(navigator, viewModel) }
    private val playlistAdapter by lazyFast { SearchFragmentNestedAdapter(navigator, viewModel) }
    private val folderAdapter by lazyFast { SearchFragmentNestedAdapter(navigator, viewModel) }

    private val mainDataObserver by lazyFast { MainDataObserver() }

    override fun onDetach() {
        val fragmentManager = activity?.supportFragmentManager
        act.fragmentTransaction {
            fragmentManager?.findFragmentByTag(FragmentTags.DETAIL)?.let { show(it) }
                    ?: fragmentManager!!.findFragmentByTag(FragmentTags.CATEGORIES)?.let { show(it) }
            setReorderingAllowed(true)
        }
        super.onDetach()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)

        viewModel.data.subscribe(viewLifecycleOwner, adapter::submitList)
        viewModel.albumsData.subscribe(viewLifecycleOwner, albumAdapter::submitList)
        viewModel.artistsData.subscribe(viewLifecycleOwner, artistAdapter::submitList)
        viewModel.foldersData.subscribe(viewLifecycleOwner, folderAdapter::submitList)
        viewModel.playlistData.subscribe(viewLifecycleOwner, playlistAdapter::submitList)
        viewModel.genreData.subscribe(viewLifecycleOwner, genreAdapter::submitList)

        RxTextView.afterTextChangeEvents(view.editText)
                .debounceFirst(250, TimeUnit.MILLISECONDS)
                .map { it.editable()!!.toString() }
                .filter { it.isBlank() || it.trim().length >= 2 }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(viewLifecycleOwner) {
                    viewModel.updateFilter(it)
                }
    }

    override fun onResume() {
        super.onResume()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        keyboard.setOnClickListener { ImeUtils.showIme(editText) }
        adapter.registerAdapterDataObserver(mainDataObserver)
    }

    override fun onPause() {
        super.onPause()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        keyboard.setOnClickListener(null)
        adapter.unregisterAdapterDataObserver(mainDataObserver)
    }

    override fun setupNestedList(layoutId: Int, recyclerView: RecyclerView) {
        when (layoutId) {
            R.layout.item_search_folder_horizontal_list -> {
                setupHorizontalList(recyclerView, folderAdapter)
            }
            R.layout.item_search_albums_horizontal_list -> {
                setupHorizontalList(recyclerView, albumAdapter)
            }
            R.layout.item_search_artists_horizontal_list -> {
                setupHorizontalList(recyclerView, artistAdapter)
            }
            R.layout.item_search_playlists_horizontal_list -> {
                setupHorizontalList(recyclerView, playlistAdapter)
            }
            R.layout.item_search_genre_horizontal_list -> {
                setupHorizontalList(recyclerView, genreAdapter)
            }
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        val layoutManager = LinearLayoutManager(
                list.context,
                LinearLayoutManager.HORIZONTAL, false
        )
        list.layoutManager = layoutManager
        list.adapter = adapter
        list.setHasFixedSize(true)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(list)
    }

    inner class MainDataObserver : RecyclerView.AdapterDataObserver() {

        private var job: Job? = null
        private val debounce = 50L

        init {
            updateLayoutVisibility(adapter.currentList?.snapshot())
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onDataChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onDataChanged()
        }

        private fun onDataChanged() {
            job?.cancel()
            job = GlobalScope.launch(Dispatchers.Main) {
                delay(debounce)
                yield()
                updateLayoutVisibility(adapter.currentList?.snapshot())
            }
        }

        private fun updateLayoutVisibility(list: List<*>?) {
            if (list == null) {
                return
            }
            val itemCount = list.size
            val isEmpty = itemCount == 0
            val queryLength = editText.text.toString().length
            view!!.searchImage.toggleVisibility(isEmpty && queryLength < 2, true)
            view!!.list.toggleVisibility(!isEmpty, true)

            val showEmptyState = isEmpty && queryLength >= 2
            view!!.emptyStateText.toggleVisibility(showEmptyState, true)
            view!!.emptyStateImage.toggleVisibility(showEmptyState, true)
            if (showEmptyState) {
                view!!.emptyStateImage.resumeAnimation()
            } else {
                view!!.emptyStateImage.progress = 0f
            }
        }

    }

    override fun onStop() {
        super.onStop()
        ImeUtils.hideIme(editText)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_search

}