package dev.olog.msc.presentation.related.artists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.related.artists.di.inject
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.ui.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_related_artist.*
import kotlinx.android.synthetic.main.fragment_related_artist.view.*
import javax.inject.Inject

class RelatedArtistFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<RelatedArtistFragmentViewModel>(
            factory
        )
    }

    @Inject
    lateinit var navigator: Navigator

    private val adapter by lazy { RelatedArtistFragmentAdapter(navigator) }

    override fun injectComponent() {
        inject()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = GridLayoutManager(context!!, 2)
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        setupListInset(view.list)
        viewModel.data.subscribe(viewLifecycleOwner, adapter::submitList)

        viewModel.observeTitle().subscribe(viewLifecycleOwner) { itemTitle ->
            val headersArray = resources.getStringArray(R.array.related_artists_header)
            val header = String.format(headersArray[viewModel.itemOrdinal], itemTitle)
            this.header.text = header
        }
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_related_artist
}