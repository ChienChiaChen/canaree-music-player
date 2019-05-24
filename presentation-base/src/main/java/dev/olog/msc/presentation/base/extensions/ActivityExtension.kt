@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.presentation.base.extensions

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.shared.utils.isP

inline fun FragmentActivity.fragmentTransaction(crossinline func: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}

fun FragmentTransaction.hideFragmentsIfExists(activity: FragmentActivity, tags: List<String>){
    val manager = activity.supportFragmentManager
    tags.forEach { tag ->
        manager.findFragmentByTag(tag)?.let { hide(it) }
    }
}

fun FragmentActivity.getTopFragment(): Fragment? {
    val topFragment = supportFragmentManager.backStackEntryCount - 1
    if (topFragment > -1){
        val tag = supportFragmentManager.getBackStackEntryAt(topFragment).name
        return supportFragmentManager.findFragmentByTag(tag)
    }
    return null
}

@SuppressLint("NewApi")
inline fun View.hasNotch(): Boolean {
    if (isP()){
        return rootWindowInsets?.displayCutout != null
    }
    return false
}