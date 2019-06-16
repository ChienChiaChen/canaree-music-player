@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import dev.olog.msc.R


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
    this.visibility = View.GONE
}

inline fun View.setVisible(){
    this.visibility = View.VISIBLE
}

inline fun View.setInvisible(){
    this.visibility = View.INVISIBLE
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

fun View.windowBackground(): Int {
    return context.themeAttributeToColor(android.R.attr.windowBackground)
}
// Used to tint buttons
fun Context.textColorTertiary(): Int {
    return this.themeAttributeToColor(android.R.attr.textColorTertiary)
}

fun Context.colorAccent(): Int {
    return themeAttributeToColor(android.R.attr.colorAccent)
}

fun Context.colorAccentId(): Int {
    return themeAttributeToResId(android.R.attr.colorAccent)
}

fun Context.textColorPrimary(): Int {
    return themeAttributeToColor(android.R.attr.textColorPrimary)
}

fun Context.textColorSecondary(): Int {
    return themeAttributeToColor(android.R.attr.textColorSecondary)
}

fun Context.windowBackground(): Int {
    return themeAttributeToColor(android.R.attr.windowBackground)
}

fun Context.scrimColor(): Int {
    return themeAttributeToColor(R.attr.scrimColor)
}

inline fun Context.colorPrimary(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorPrimary)
}

inline fun Context.colorSurface(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorSurface)
}

inline fun Context.colorControlNormal(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorControlNormal)
}

inline fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved){
        return ContextCompat.getColor(this, outValue.resourceId)
    }
    return fallbackColor
}

inline  fun Context.themeAttributeToResId(themeAttributeId: Int): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
        return outValue.resourceId
    }
    return -1
}

fun View.setHeight(@Px heightPx: Int){
    val params = this.layoutParams
    when (params){
        is FrameLayout.LayoutParams -> params.height = heightPx
        is LinearLayout.LayoutParams -> params.height = heightPx
        is RelativeLayout.LayoutParams -> params.height = heightPx
        is CoordinatorLayout.LayoutParams -> params.height = heightPx
        is ConstraintLayout.LayoutParams -> params.height = heightPx
    }
    layoutParams = params
}

fun View.setWidth(@Px heightPx: Int){
    val params = this.layoutParams
    when (params){
        is FrameLayout.LayoutParams -> params.width = heightPx
        is LinearLayout.LayoutParams -> params.width = heightPx
        is RelativeLayout.LayoutParams -> params.width = heightPx
        is CoordinatorLayout.LayoutParams -> params.width = heightPx
        is ConstraintLayout.LayoutParams -> params.width = heightPx
    }
    layoutParams = params
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

fun <T : View> View.findViewByIdNotRecursive(id: Int): T? {
    if (this is ViewGroup) {
        forEach { child ->
            if (child.id == id) {
                return child as T
            }
        }
    }
    return null
}