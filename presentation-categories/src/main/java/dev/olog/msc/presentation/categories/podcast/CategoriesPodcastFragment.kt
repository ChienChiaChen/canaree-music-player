package dev.olog.msc.presentation.categories.podcast

import android.os.Bundle
import android.view.View

import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.categories.R
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import dev.olog.presentation.base.fragment.BaseFragment
import dev.olog.presentation.base.FloatingWindowHelper
import dev.olog.presentation.base.extensions.act
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import javax.inject.Inject

class CategoriesPodcastFragment : BaseFragment() {

    companion object {
        const val TAG = "CategoriesPodcastFragment"

        @JvmStatic
        fun newInstance(): CategoriesPodcastFragment {
            return CategoriesPodcastFragment()
        }
    }

    @Inject
    lateinit var classes: Classes
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var presenter: CategoriesPodcastFragmentPresenter

    private val pagerAdapter by lazyFast {
        CategoriesPodcastFragmentViewPager(
                act.applicationContext, childFragmentManager, presenter.getCategories()
        )
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.header.text = getString(R.string.common_podcasts)
        view.viewPager.offscreenPageLimit = 3
        view.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count)

        view.pagerEmptyState.toggleVisibility(pagerAdapter.isEmpty(), true)
    }

    override fun onResume() {
        super.onResume()
        more.setOnClickListener {
            try {
                navigator.toMainPopup(requireActivity(), it, createMediaId())
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
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
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!, classes.floatingWindowService())
    }

    private val onPageChangeListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            presenter.setViewPagerLastPage(position)
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_library_categories
}