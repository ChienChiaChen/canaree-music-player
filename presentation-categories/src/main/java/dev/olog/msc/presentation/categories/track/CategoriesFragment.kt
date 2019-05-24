package dev.olog.msc.presentation.categories.track

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.base.FloatingWindowHelper
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.categories.BuildConfig
import dev.olog.msc.presentation.categories.FragmentFactory
import dev.olog.msc.presentation.categories.R
import dev.olog.msc.presentation.categories.Tutorial
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.shared.core.lazyFast
import kotlinx.android.synthetic.main.fragment_library_categories.*
import kotlinx.android.synthetic.main.fragment_library_categories.view.*
import javax.inject.Inject

class CategoriesFragment : BaseFragment() {

    companion object {

        @JvmStatic
        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

    @Inject
    lateinit var presenter: CategoriesFragmentPresenter
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var prefsGateway: AppPreferencesGateway

    private val fragmentFactory by lazyFast { FragmentFactory(childFragmentManager.fragmentFactory) }

    private val pagerAdapter by lazyFast {
        CategoriesViewPager(
            act.applicationContext, childFragmentManager, fragmentFactory,
            presenter.getCategories(), prefsGateway
        )
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.viewPager.adapter = pagerAdapter
        view.tabLayout.setupWithViewPager(view.viewPager)
        view.viewPager.currentItem = presenter.getViewPagerLastPage(pagerAdapter.count)
        view.viewPager.offscreenPageLimit = 3

        if (pagerAdapter.isEmpty()) {
            view.pagerEmptyState.visibility = View.VISIBLE
        } else {
            view.pagerEmptyState.visibility = View.GONE
        }

        if (BuildConfig.DEBUG) { // TODO remove
            view.header.setOnClickListener {
                val current = AppCompatDelegate.getDefaultNightMode()
                if (current == AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                activity?.recreate()
            }
            view.header.setOnLongClickListener {
                //                DebugProbes.dumpCoroutines()
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewPager.addOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener { navigator.toMainPopup(requireActivity(), it, createMediaId()) }
        floatingWindow.setOnClickListener { startServiceOrRequestOverlayPermission() }

        if (presenter.canShowFloatingWindowTutorial()) {
            Tutorial.floatingWindow(floatingWindow)
        }
    }

    override fun onPause() {
        super.onPause()
        viewPager.removeOnPageChangeListener(onPageChangeListener)
        more.setOnClickListener(null)
        floatingWindow.setOnClickListener(null)
    }

    private fun createMediaId(): MediaIdCategory? {
        return pagerAdapter.getCategoryAtPosition(viewPager.currentItem)
    }

    private fun startServiceOrRequestOverlayPermission() {
        FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity!!, Classes.floatingWindowService)
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