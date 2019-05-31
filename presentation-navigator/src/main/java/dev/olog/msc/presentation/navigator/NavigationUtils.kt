package dev.olog.msc.presentation.navigator

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

// fragment tag, last added
internal var backStackCount = mutableMapOf<String, Int>()

private var lastRequest: Long = -1

private val basicFragments = listOf(
    Fragments.CATEGORIES,
    Fragments.CATEGORIES_PODCAST,
    Fragments.SEARCH,
    Fragments.PLAYING_QUEUE
)

/**
 * Use this when you can instantiate multiple times same fragment
 */
internal fun createBackStackTag(fragmentTag: String): String {
    // get last + 1
    val counter = backStackCount.getOrPut(fragmentTag) { 0 } + 1
    // update
    backStackCount[fragmentTag] = counter
    // creates new
    return "$fragmentTag$counter"
}

internal fun allowed(): Boolean {
    val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
    lastRequest = System.currentTimeMillis()
    return allowed
}

internal fun findFirstVisibleFragment(fragmentManager: FragmentManager): Fragment? {
    var topFragment = fragmentManager.getTopFragment()
    if (topFragment == null) {
        topFragment = fragmentManager.fragments
            .filter { it.isVisible }
            .firstOrNull { basicFragments.contains(it.tag) }
    }
    if (topFragment == null) {
        Log.e("Navigator", "Something went wrong, for some reason no fragment were found")
    }
    return topFragment
}