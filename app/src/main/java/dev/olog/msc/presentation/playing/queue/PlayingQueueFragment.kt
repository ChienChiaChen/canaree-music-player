package dev.olog.msc.presentation.playing.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import dev.olog.msc.R
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import dev.olog.presentation.base.fragment.BaseFragment
import dev.olog.presentation.base.FloatingWindowHelper
import dev.olog.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.presentation.base.extensions.act
import dev.olog.presentation.base.extensions.ctx
import dev.olog.presentation.base.extensions.subscribe
import dev.olog.presentation.base.extensions.viewModelProvider
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"

        @JvmStatic
        fun newInstance(): PlayingQueueFragment {
            return PlayingQueueFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var adapter: PlayingQueueFragmentAdapter
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var classes: Classes
    private lateinit var layoutManager: androidx.recyclerview.widget.LinearLayoutManager

    private val viewModel by lazyFast { act.viewModelProvider<PlayingQueueFragmentViewModel>(viewModelFactory) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter.onFirstEmission {
            layoutManager.scrollToPositionWithOffset(viewModel.getCurrentPosition(), ctx.dip(20))
        }
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.RIGHT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        viewModel.data.subscribe(viewLifecycleOwner) {
            adapter.updateDataSet(it)
            view.emptyStateText.toggleVisibility(it.isEmpty(), true)
        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener {
            try {
                navigator.toMainPopup(requireActivity(), it, MediaIdCategory.PLAYING_QUEUE)
            } catch (ex: Throwable){
                ex.printStackTrace()
            }
        }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!, classes.floatingWindowService())
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}