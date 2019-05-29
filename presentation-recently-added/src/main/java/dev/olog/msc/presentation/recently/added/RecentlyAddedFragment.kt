package dev.olog.msc.presentation.recently.added

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import dev.olog.msc.app.injection.navigator.Navigator
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.MediaProvider
import dev.olog.msc.presentation.base.list.drag.TouchHelperAdapterCallback
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.ui.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_recently_added.*
import kotlinx.android.synthetic.main.fragment_recently_added.view.*
import javax.inject.Inject

class RecentlyAddedFragment : BaseFragment() {

    companion object {
        const val TAG = "RecentlyAddedFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"


        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val adapter by lazyFast {
        RecentlyAddedFragmentAdapter(navigator, activity as MediaProvider)
    }

    private val viewModel by lazyFast {
        viewModelProvider<RecentlyAddedFragmentViewModel>(
            viewModelFactory
        )
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.list.setHasFixedSize(true)

        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.LEFT)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)

        viewModel.data.subscribe(viewLifecycleOwner, adapter::submitList)

        viewModel.observeTitle().subscribe(viewLifecycleOwner) { itemTitle ->
            val headersArray = resources.getStringArray(R.array.recently_added_header)
            val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
            this.header.text = header
        }
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_recently_added
}