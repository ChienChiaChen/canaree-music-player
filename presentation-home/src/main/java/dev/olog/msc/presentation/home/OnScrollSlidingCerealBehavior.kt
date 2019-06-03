package dev.olog.msc.presentation.home

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.msc.presentation.base.extensions.panelHeight
import dev.olog.msc.presentation.base.interfaces.SuperCerealBottomSheetBehavior
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.ui.extensions.findViewByIdNotRecursive
import dev.olog.msc.shared.utils.clamp
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Adjust toolbar, tab layout(if present), bottom navigation and sliding panel sliding when a list
 * is scrollied
 *
 * This class assumes each fragment has a unique tag
 */
class OnScrollSlidingCerealBehavior @Inject constructor(private val activity: AppCompatActivity) : DefaultLifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    private val slidingPanel by lazyFast { BottomSheetBehavior.from(activity.slidingPanel) as SuperCerealBottomSheetBehavior }
    private val bottomNavigation by lazyFast { activity.bottomWrapper }
    private val blurView: View? by lazyFast { activity.blurView }
    private var toolbarMap = SparseArray<View>()
    private var tabLayoutMap = SparseArray<View>()
    private var fabMap = SparseArray<View>()
    private var viewPagerListenerMap = SparseArray<ViewPagerListener>()

    private val superCerialSlidingPanelListener by lazyFast { SuperCerealBottomSheetCallback() }

    private val slidingPanelHeight by lazyFast {
        activity.dimen(R.dimen.sliding_panel_peek)
    }
    private val slidingPanelPlusNavigationHeight by lazyFast {
        activity.dimen(R.dimen.sliding_panel_peek) + activity.dimen(R.dimen.bottom_navigation_height)
    }

    override fun onResume(owner: LifecycleOwner) {
        slidingPanel.addPanelSlideListener(superCerialSlidingPanelListener)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(callbacks, true)
    }

    override fun onPause(owner: LifecycleOwner) {
        slidingPanel.removePanelSlideListener(superCerialSlidingPanelListener)
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(callbacks)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            var clampedToolbarTranslation = 0f
            toolbarMap.get(recyclerView.hashCode())?.let { toolbar ->
                clampedToolbarTranslation = clamp(toolbar.translationY - dy, -toolbar.height.toFloat(), 0f)
                toolbar.translationY = clampedToolbarTranslation
            }
            tabLayoutMap.get(recyclerView.hashCode())?.let { tabLayout ->
                tabLayout.translationY = clampedToolbarTranslation
            }

            val clampedNavigationTranslation =
                    clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())
            val clampedSlidingPanelTranslationY = clamp(
                    slidingPanelPlusNavigationHeight - clampedNavigationTranslation.toInt(),
                    slidingPanelHeight,
                    slidingPanelPlusNavigationHeight
            )
            slidingPanel.panelHeight = clampedSlidingPanelTranslationY
            blurView?.translationY = clampedNavigationTranslation
            bottomNavigation.translationY = clampedNavigationTranslation

            fabMap.forEach { key, value ->
                // not very taxing because this collection will have few elements inside
                value.translationY = clampedNavigationTranslation
            }
        }
    }

    // used to restore scrolling when the list contains too few items
    private inner class ViewPagerListener(private val fm: FragmentManager) : ViewPager.SimpleOnPageChangeListener(){

        override fun onPageSelected(position: Int) {
            val fragment = fm.fragments.find { it.tag?.last().toString() == position.toString() }
            val recyclerView = fragment?.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)
                    ?: throw IllegalStateException("view pager child has not a list")

            when (val layoutManager = recyclerView.layoutManager){
                // check first if is a 'GridLayoutManager' because it extends 'LinearLayoutManager'
                is GridLayoutManager -> handleGridLayoutManager(recyclerView, layoutManager)
                is LinearLayoutManager -> handleLinearLayoutManager(recyclerView, layoutManager)
            }
        }

        // song and podcast case
        private fun handleLinearLayoutManager(recyclerView: RecyclerView, layoutManager: LinearLayoutManager){
            if (recyclerView.adapter?.itemCount ?: 0 == layoutManager.findLastVisibleItemPosition() + 1){
                // there are no items offscreen
                restore(recyclerView)
            }
        }

        private fun handleGridLayoutManager(recyclerView: RecyclerView, layoutManager: GridLayoutManager){
            if (recyclerView.adapter?.itemCount ?: 0 == layoutManager.findLastVisibleItemPosition() + 1){
                // there are no items offscreen
                restore(recyclerView)
            }
        }

        private fun restore(recyclerView: RecyclerView){
            blurView?.animate()?.translationY(0f)
            tabLayoutMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
            toolbarMap.get(recyclerView.hashCode())?.animate()?.translationY(0f)
            bottomNavigation.animate().translationY(0f)
            slidingPanel.panelHeight = slidingPanelPlusNavigationHeight
        }

    }

    private inner class SuperCerealBottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {

        private var lastState = BottomSheetBehavior.STATE_COLLAPSED
        private var lastCollapsedTranslationY = bottomNavigation.translationY

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val translationY = clamp(
                    bottomNavigation.height * clamp(slideOffset, 0f, 1f),
                    lastCollapsedTranslationY,
                    bottomNavigation.height.toFloat()
            )
            bottomNavigation.translationY = translationY
        }

        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (lastState == BottomSheetBehavior.STATE_COLLAPSED && newState == BottomSheetBehavior.STATE_DRAGGING) {
                lastCollapsedTranslationY = bottomNavigation.translationY
            }
            lastState = newState
        }
    }


    private val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {

        // TODO check after migratin to viewpager 2
        private fun isViewPagerChildTag(tag: String?) = tag?.startsWith("android:switcher:") == true

        private fun hasFragmentOwnership(tag: String?) = tag?.startsWith("dev.olog.msc") == true
        private fun isPlayerTag(tag: String?) = tag?.contains("Player") == true

        private fun couldHaveToolbar(f: Fragment): Boolean {
            return hasFragmentOwnership(f.tag) && !isPlayerTag(f.tag)
        }

        override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
            println("on fragment resumed ${fragment.tag}")
            if (isPlayerTag(fragment.tag)) {
                return
            }

            searchForViewPager(fragment)?.let { viewPager ->
                val listener = ViewPagerListener(fragment.childFragmentManager)
                viewPagerListenerMap.append(viewPager.hashCode(), listener)
                viewPager.addOnPageChangeListener(listener)
            }

            val recyclerView = searchForRecyclerView(fragment)
            if (recyclerView != null) {
                addOnScrollListener(fragment, recyclerView)
                searchForToolbarDefault(fragment,
                        onToolbarFound = {
                            toolbarMap.append(recyclerView.hashCode(), it)
                        },
                        onTabLayoutFound = {
                            tabLayoutMap.append(recyclerView.hashCode(), it)
                        }
                )
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
            println("on fragment paused ${fragment.tag}")
            if (isPlayerTag(fragment.tag)) {
                return
            }

            searchForViewPager(fragment)?.let { viewPager ->
                val listener = viewPagerListenerMap.get(viewPager.hashCode())
                viewPager.removeOnPageChangeListener(listener)
                viewPagerListenerMap.remove(viewPager.hashCode())
            }

            val recyclerView = searchForRecyclerView(fragment)
            if (recyclerView != null) {
                recyclerView.removeOnScrollListener(onScrollListener)
                fabMap.remove(recyclerView.hashCode().hashCode())

                searchForToolbarDefault(
                        fragment,
                        onToolbarFound = {
                            toolbarMap.remove(recyclerView.hashCode())
                        },
                        onTabLayoutFound = {
                            tabLayoutMap.remove(recyclerView.hashCode())
                        }
                )
            }
        }

        /**
         * All main recycler view in the app have [android:id] = [R.id.list] and are
         *  placed as direct child of root.
         * If fails to find R.id.list, try recursiveley to find [R.id.recycler_view]
         *  in the hierarchy (settings fragment)
         */
        private fun searchForRecyclerView(f: Fragment): RecyclerView? {
            var recyclerView = f.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)
            if (recyclerView == null && f.tag == Fragments.SETTINGS) {
                recyclerView = f.view?.findViewById(R.id.recycler_view)
            }
            return recyclerView
        }

        private fun searchForViewPager(f: Fragment): ViewPager? {
            if (f.tag == Fragments.CATEGORIES || f.tag == Fragments.CATEGORIES_PODCAST){
                return f.view?.findViewByIdNotRecursive(R.id.viewPager)
            }
            return null
        }

        private fun addOnScrollListener(f: Fragment, recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(onScrollListener)

            val fab = f.view?.findViewById<View>(R.id.fab)
            if (fab != null && fab.isVisible) {
                // add only visible fabs
                fabMap.append(recyclerView.hashCode(), fab)
            }
            println("adding scroll listener to ${f.tag}")
        }

        private fun searchForToolbarDefault(f: Fragment,
                                            onToolbarFound: (View) -> Unit,
                                            onTabLayoutFound: (View) -> Unit) {
            val view : View? = when {
                isViewPagerChildTag(f.tag) -> {
                    // search toolbar and tab layout in parent fragment
                    f.parentFragment?.view
                }
                couldHaveToolbar(f) -> f.view
                else -> null
            }
            // since it can be only 1 tab layout and toolbar per visible screen,
            // just override the last value when find a new one tab layout or toolbar
            view?.findViewByIdNotRecursive<View>(R.id.tabLayout)?.apply {
                onTabLayoutFound(this)

            }
            view?.findViewByIdNotRecursive<View>(R.id.toolbar)?.apply {
                onToolbarFound(this)
            }
        }
    }

}