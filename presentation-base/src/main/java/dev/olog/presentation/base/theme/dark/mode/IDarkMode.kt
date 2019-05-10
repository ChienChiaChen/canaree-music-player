package dev.olog.presentation.base.theme.dark.mode

import android.app.Activity

interface IDarkMode {
    fun isWhiteMode(): Boolean
    fun isGrayMode(): Boolean
    fun isDarkMode(): Boolean
    fun isBlackMode(): Boolean

    fun setCurrentActivity(activity: Activity?)
}