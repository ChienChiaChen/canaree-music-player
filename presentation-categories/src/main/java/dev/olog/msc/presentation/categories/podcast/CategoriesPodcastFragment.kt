package dev.olog.msc.presentation.categories.podcast

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.FloatingWindowHelper
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.categories.R
import dev.olog.msc.presentation.categories.di.inject
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.navigator.Services
import dev.olog.msc.shared.core.lazyFast
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import javax.inject.Inject

class CategoriesPodcastFragment : BaseFragment() {

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var presenter: CategoriesPodcastFragmentPresenter

    private val pagerAdapter by lazyFast {
        CategoriesPodcastFragmentViewPager(act, childFragmentManager, presenter.getCategories())
    }

    override fun injectComponent() {
        inject()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.header.text = getString(R.string.common_podcasts)
        view.viewPager.offscreenPageLimit = 3
        view.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count)

        if (pagerAdapter.isEmpty()) {
            view.pagerEmptyState.visibility = View.VISIBLE
        } else {
            view.pagerEmptyState.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener { navigator.toMainPopup(requireActivity(), it, createMediaId()) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }
        viewPager.addOnPageChangeListener(onPageChangeListener)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    override fun onPause() {
        super.onPause()
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
        viewPager.removeOnPageChangeListener(onPageChangeListener)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!, Services.floating())
    }

    private val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {

        override fun onPageSelected(position: Int) {
            presenter.setViewPagerLastPage(position)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_categories
}