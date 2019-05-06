package dev.olog.msc.shared.ui.extensions

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.msc.shared.ui.ThemedDialog
import dev.olog.msc.shared.utils.isP

fun FragmentActivity.fragmentTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
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
fun View.hasNotch(): Boolean {
    if (isP()){
        return rootWindowInsets?.displayCutout != null
    }
    return false
}

fun FragmentActivity.simpleDialog(builder: AlertDialog.Builder.() -> AlertDialog.Builder){
    ThemedDialog.builder(this)
            .builder()
            .show()
}