@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.ui.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

fun View.toggleVisibility(visible: Boolean, gone: Boolean){
    if (visible){
        this.visibility = View.VISIBLE
    } else {
        if (gone){
            this.visibility = View.GONE
        } else {
            this.visibility = View.INVISIBLE
        }
    }
}

inline fun View.setGone(){
    if (this.visibility != View.GONE){
        this.visibility = View.GONE
    }
}

inline fun View.setVisible(){
    if (this.visibility != View.VISIBLE){
        this.visibility = View.VISIBLE
    }
}

inline fun View.setInvisible(){
    if (this.visibility != View.INVISIBLE){
        this.visibility = View.INVISIBLE
    }
}

fun View.setPaddingTop(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
}

inline fun View.setPaddingBottom(padding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, padding)
}

inline fun View.toggleSelected(){
    this.isSelected = !this.isSelected
}

fun View.colorSurface(): Int {
    return context.themeAttributeToColor(android.R.attr.windowBackground)
}

fun ViewGroup.findChild(filter: (View) -> Boolean): View?{
    var child : View? = null

    forEachRecursively {
        if (filter(it)){
            child = it
            return@forEachRecursively
        }
    }

    return child
}

fun ViewGroup.forEachRecursively(action: (view: View) -> Unit){
    forEach {
        if (it is ViewGroup){
            it.forEachRecursively(action)
        } else {
            action(it)
        }
    }
}