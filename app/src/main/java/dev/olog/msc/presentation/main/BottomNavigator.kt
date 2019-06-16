package dev.olog.msc.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.R
import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment
import dev.olog.msc.presentation.main.utils.toFragmentTag
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.search.SearchFragment
import dev.olog.msc.utils.k.extension.fragmentTransaction

internal object BottomNavigator {

    private val tags = listOf(
        CategoriesFragment.TAG,
        CategoriesPodcastFragment.TAG,
        SearchFragment.TAG,
        PlayingQueueFragment.TAG
    )

    fun initialize(activity: FragmentActivity, page: BottomNavigationPage) {
        for (fragment in activity.supportFragmentManager.fragments) {
            if (tags.contains(fragment.tag)) {
                // fragment alreade added
                return
            }
        }
        val newFragmentTag = page.toFragmentTag()

        activity.fragmentTransaction {
            for (tag in tags) {
                if (tag != newFragmentTag) {
                    val fragment = tagToInstance(tag)
                    add(R.id.fragmentContainer, fragment, tag)
                    hide(fragment)
                }
            }
            setReorderingAllowed(true)
        }
    }

    fun navigate(activity: FragmentActivity, page: BottomNavigationPage) {
        val fragmentTag = page.toFragmentTag()

        if (!tags.contains(fragmentTag)) {
            throw IllegalArgumentException("invalid fragment tag $fragmentTag")
        }

        for (index in 0..activity.supportFragmentManager.backStackEntryCount) {
            // clear the backstack
            activity.supportFragmentManager.popBackStack()
        }

        activity.fragmentTransaction {
            disallowAddToBackStack()
            setReorderingAllowed(true)
            // hide other categories fragment
            activity.supportFragmentManager.fragments
                .asSequence()
                .filter { tags.contains(it.tag) }
                .forEach { hide(it) }

            setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment == null) {
                add(R.id.fragmentContainer, tagToInstance(fragmentTag), fragmentTag)
            } else {
                show(fragment)
            }
        }
    }

    private fun tagToInstance(tag: String): Fragment = when (tag) {
        CategoriesFragment.TAG -> CategoriesFragment.newInstance()
        CategoriesPodcastFragment.TAG -> CategoriesPodcastFragment.newInstance()
        SearchFragment.TAG -> SearchFragment.newInstance()
        PlayingQueueFragment.TAG -> PlayingQueueFragment.newInstance()
        else -> throw IllegalArgumentException("invalid fragment tag $tag")
    }

}