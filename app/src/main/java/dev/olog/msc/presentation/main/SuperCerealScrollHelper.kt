package dev.olog.msc.presentation.main

import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import dev.olog.msc.R
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment
import dev.olog.msc.utils.k.extension.findViewByIdNotRecursive
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.ScrollHelper

class SuperCerealScrollHelper(
    activity: MainActivity,
    input: Input.Full
) : ScrollHelper(activity, input) {

//    private val blurView: View? by lazyFast { activity.blurView }

    private val bottomNavigation = input.bottomNavigation.first

    override fun applyInsetsToList(fragment: Fragment, list: RecyclerView, toolbar: View?, tabLayout: View?) {
        super.applyInsetsToList(fragment, list, toolbar, tabLayout)
        if (fragment.tag?.startsWith(DetailFragment.TAG) == true){
            // apply only bottom padding
            list.updatePadding(top = 0)
        }
    }

//    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//        super.onRecyclerViewScrolled(recyclerView, dx, dy)
//        val clampedNavigationTranslation =
//            clamp(bottomNavigation.translationY + dy, 0f, bottomNavigation.height.toFloat())
//        blurView?.translationY = clampedNavigationTranslation
//    }

//    override fun restoreInitialPosition(recyclerView: RecyclerView) {
//        super.restoreInitialPosition(recyclerView)
//        blurView?.animate()?.translationY(0f)
//    }

    override fun searchForFab(fragment: Fragment): View? {
        return fragment.view?.findViewById(R.id.fab)
    }

    override fun searchForRecyclerView(fragment: Fragment): RecyclerView? {
        var recyclerView = fragment.view?.findViewByIdNotRecursive<RecyclerView>(R.id.list)
//        if (recyclerView == null && fragment.tag == Fragments.SETTINGS) { TODO
//            recyclerView = fragment.view?.findViewById(R.id.recycler_view)
//        }
        return recyclerView
    }

    override fun searchForTabLayout(fragment: Fragment): View? {
        val view : View? = when {
            isViewPagerChildTag(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            else -> fragment.view
        }
        return view?.findViewById(R.id.tabLayout)
    }

    override fun searchForToolbar(fragment: Fragment): View? {
        val view : View? = when {
            isViewPagerChildTag(fragment.tag) -> {
                // search toolbar and tab layout in parent fragment
                fragment.parentFragment?.view
            }
            else -> fragment.view
        }
        return view?.findViewById(R.id.toolbar)
    }

    override fun searchForViewPager(fragment: Fragment): ViewPager? {
        val tag = fragment.tag
        if (tag == CategoriesFragment.TAG || tag == CategoriesPodcastFragment.TAG){
            val view = fragment.view?.findViewByIdNotRecursive<ViewPager>(R.id.viewPager)
            return view
        }
        return null
    }

    override fun skipFragment(fragment: Fragment): Boolean {
        if (isViewPagerChildTag(fragment.tag)){
            return false
        }
        return isPlayerTag(fragment.tag) || !hasFragmentOwnership(fragment.tag)
    }

    // TODO check after migratin to viewpager 2
    private fun isViewPagerChildTag(tag: String?) = tag?.startsWith("android:switcher:") == true

    private fun hasFragmentOwnership(tag: String?) = tag?.startsWith("dev.olog.msc") == true

    private fun isPlayerTag(tag: String?) = tag?.contains("Player") == true
}