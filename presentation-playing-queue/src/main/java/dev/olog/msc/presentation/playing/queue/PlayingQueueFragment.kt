package dev.olog.msc.presentation.playing.queue

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.FloatingWindowHelper
import dev.olog.msc.presentation.base.drag.OnStartDragListener
import dev.olog.msc.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.playing.queue.adapter.PlayingQueueFragmentAdapter
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_playing_queue.*
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment(), OnStartDragListener {

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
    lateinit var navigator: Navigator

    private lateinit var layoutManager: LinearLayoutManager

    private val adapter by lazyFast { PlayingQueueFragmentAdapter(navigator, this) }

    private val viewModel by lazyFast { act.viewModelProvider<PlayingQueueFragmentViewModel>(viewModelFactory) }

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.RIGHT)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper!!.attachToRecyclerView(view.list)

        var emitted = false

        viewModel.data.subscribe(viewLifecycleOwner) {
            adapter.submitList(it) {
                if (!emitted) {
                    layoutManager.scrollToPositionWithOffset(viewModel.getCurrentPosition(), ctx.dip(20))
                    emitted = true
                }
            }
//            view.emptyStateText.toggleVisibility(it.isEmpty(), true) TODO empty list
        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener {
            try {
                navigator.toMainPopup(requireActivity(), it, MediaIdCategory.PLAYING_QUEUE)
            } catch (ex: Throwable) {
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
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!, Classes.floatingWindowService)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue


}