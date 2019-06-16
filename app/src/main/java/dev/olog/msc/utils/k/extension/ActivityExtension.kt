package dev.olog.msc.utils.k.extension

import androidx.appcompat.app.AlertDialog
import dev.olog.msc.presentation.theme.ThemedDialog

fun androidx.fragment.app.FragmentActivity.fragmentTransaction(func: androidx.fragment.app.FragmentTransaction.() -> androidx.fragment.app.FragmentTransaction) {
    supportFragmentManager
            .beginTransaction()
            .func()
            .commitAllowingStateLoss()
}

fun androidx.fragment.app.FragmentTransaction.hideFragmentsIfExists(activity: androidx.fragment.app.FragmentActivity, tags: List<String>){
    val manager = activity.supportFragmentManager
    tags.forEach { tag ->
        manager.findFragmentByTag(tag)?.let { hide(it) }
    }
}

fun androidx.fragment.app.FragmentActivity.getTopFragment(): androidx.fragment.app.Fragment? {
    val topFragment = supportFragmentManager.backStackEntryCount - 1
    if (topFragment > -1){
        val tag = supportFragmentManager.getBackStackEntryAt(topFragment).name
        return supportFragmentManager.findFragmentByTag(tag)
    }
    return null
}

fun androidx.fragment.app.FragmentActivity.simpleDialog(builder: AlertDialog.Builder.() -> AlertDialog.Builder){
    ThemedDialog.builder(this)
            .builder()
            .show()
}