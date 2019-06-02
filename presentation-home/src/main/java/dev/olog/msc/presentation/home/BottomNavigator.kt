package dev.olog.msc.presentation.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.presentation.base.extensions.fragmentTransaction
import dev.olog.msc.presentation.home.utils.toFragmentTag
import dev.olog.msc.presentation.navigator.Fragments

internal object BottomNavigator {

    private val tags = listOf(
        Fragments.CATEGORIES,
        Fragments.CATEGORIES_PODCAST,
        Fragments.SEARCH,
        Fragments.PLAYING_QUEUE
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
                if (tag != newFragmentTag){
                    val fragment = tagToInstance(activity, tag)
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
                add(R.id.fragmentContainer, tagToInstance(activity, fragmentTag), fragmentTag)
            } else {
                show(fragment)
            }
        }
    }

    private fun tagToInstance(context: Context, tag: String): Fragment = when (tag) {
        Fragments.CATEGORIES -> Fragments.categories(
            context
        )
        Fragments.CATEGORIES_PODCAST -> Fragments.categoriesPodcast(
            context
        )
        Fragments.SEARCH -> Fragments.search(
            context
        )
        Fragments.PLAYING_QUEUE -> Fragments.playingQueue(
            context
        )
        else -> throw IllegalArgumentException("invalid fragment tag $tag")
    }

}