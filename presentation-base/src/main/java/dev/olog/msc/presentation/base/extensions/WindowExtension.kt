package dev.olog.msc.presentation.base.extensions

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.Window
import dev.olog.msc.presentation.base.R
import dev.olog.msc.shared.ui.theme.immersive
import dev.olog.msc.shared.utils.isMarshmallow
import dev.olog.msc.shared.utils.isOreo

fun Window.setLightStatusBar(){
    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (context.immersive().isEnabled()){
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    if (isMarshmallow() && !isDarkMode(context)){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (isOreo()){
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            navigationBarColor = Color.WHITE
        }
    }
    decorView.systemUiVisibility = flags
}

fun Window.removeLightStatusBar(){

    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (context.immersive().isEnabled()){
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    if (isOreo() && !isDarkMode(context)){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        navigationBarColor = Color.WHITE
    }
    decorView.systemUiVisibility = flags
}

private fun isDarkMode(context: Context): Boolean{
    return context.resources.getBoolean(R.bool.is_dark_mode)
}