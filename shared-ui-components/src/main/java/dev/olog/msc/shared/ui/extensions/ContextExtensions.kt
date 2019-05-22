@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.ui.extensions

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.R as materialR

inline fun Context.getAnimatedVectorDrawable(@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
}

inline fun Context.colorAccentId(): Int {
    return themeAttributeToResId(android.R.attr.colorAccent)
}

inline fun Context.textColorPrimary(): Int {
    return themeAttributeToColor(android.R.attr.textColorPrimary)
}

inline fun Context.textColorSecondary(): Int {
    return themeAttributeToColor(android.R.attr.textColorSecondary)
}

inline fun Context.colorPrimary(): Int {
    return themeAttributeToColor(materialR.attr.colorPrimary)
}

inline fun Context.colorPrimaryVariant(): Int {
    return themeAttributeToColor(materialR.attr.colorPrimaryVariant)
}

inline fun Context.colorSecondary(): Int {
    return themeAttributeToColor(materialR.attr.colorSecondary)
}

inline fun Context.colorSurface(): Int {
    return themeAttributeToColor(materialR.attr.colorSurface)
}

inline fun Context.colorControlNormal(): Int {
    return themeAttributeToColor(materialR.attr.colorControlNormal)
}

fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
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