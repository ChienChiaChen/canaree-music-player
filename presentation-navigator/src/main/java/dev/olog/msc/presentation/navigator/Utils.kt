package dev.olog.msc.presentation.navigator

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

internal inline fun FragmentActivity.fragmentTransaction(crossinline func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
        .beginTransaction()
        .func()
        .commitAllowingStateLoss()
}

internal fun FragmentTransaction.hideFragmentsIfExists(activity: FragmentActivity, tags: List<String>){
    val manager = activity.supportFragmentManager
    tags.forEach { tag ->
        manager.findFragmentByTag(tag)?.let { hide(it) }
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any>) : T {
    arguments = bundleOf(*params)
    return this
}