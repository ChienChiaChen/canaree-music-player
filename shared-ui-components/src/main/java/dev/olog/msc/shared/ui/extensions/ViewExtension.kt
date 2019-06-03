@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.ui.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
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

inline fun View.toggleSelected(){
    this.isSelected = !this.isSelected
}

fun View.colorSurface(): Int {
    return context.themeAttributeToColor(android.R.attr.windowBackground)
}

fun View.setMargin(leftPx: Int = -1, topPx: Int = -1, rightPx: Int = -1, bottomPx: Int = -1){
    val params = this.layoutParams
    when (params){
        is FrameLayout.LayoutParams -> {
            params.leftMargin = if (leftPx == -1) params.leftMargin else leftPx
            params.rightMargin = if (rightPx == -1) params.rightMargin else rightPx
            params.topMargin = if (top == -1) params.topMargin else topPx
            params.bottomMargin = if (bottomPx == -1) params.bottomMargin else bottomPx
        }
        is LinearLayout.LayoutParams -> {
            params.leftMargin = if (leftPx == -1) params.leftMargin else leftPx
            params.rightMargin = if (rightPx == -1) params.rightMargin else rightPx
            params.topMargin = if (top == -1) params.topMargin else topPx
            params.bottomMargin = if (bottomPx == -1) params.bottomMargin else bottomPx
        }
        is RelativeLayout.LayoutParams -> {
            params.leftMargin = if (leftPx == -1) params.leftMargin else leftPx
            params.rightMargin = if (rightPx == -1) params.rightMargin else rightPx
            params.topMargin = if (top == -1) params.topMargin else topPx
            params.bottomMargin = if (bottomPx == -1) params.bottomMargin else bottomPx
        }
        is CoordinatorLayout.LayoutParams -> {
            params.leftMargin = if (leftPx == -1) params.leftMargin else leftPx
            params.rightMargin = if (rightPx == -1) params.rightMargin else rightPx
            params.topMargin = if (top == -1) params.topMargin else topPx
            params.bottomMargin = if (bottomPx == -1) params.bottomMargin else bottomPx
        }
        is ConstraintLayout.LayoutParams -> {
            params.leftMargin = if (leftPx == -1) params.leftMargin else leftPx
            params.rightMargin = if (rightPx == -1) params.rightMargin else rightPx
            params.topMargin = if (top == -1) params.topMargin else topPx
            params.bottomMargin = if (bottomPx == -1) params.bottomMargin else bottomPx
        }
    }
    layoutParams = params
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