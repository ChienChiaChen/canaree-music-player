package dev.olog.msc.shared.ui.extensions

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
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

fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved){
        return ContextCompat.getColor(this, outValue.resourceId)
    }
    return fallbackColor
}

fun Context.themeAttributeToResId(themeAttributeId: Int): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
        return outValue.resourceId
    }
    return -1
}