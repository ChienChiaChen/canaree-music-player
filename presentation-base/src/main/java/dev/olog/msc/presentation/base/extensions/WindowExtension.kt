package dev.olog.msc.presentation.base.extensions

import android.graphics.Color
import android.view.View
import android.view.Window
import dev.olog.msc.presentation.base.theme.dark.mode.isDark
import dev.olog.msc.shared.ui.theme.HasImmersive
import dev.olog.msc.shared.utils.isMarshmallow
import dev.olog.msc.shared.utils.isOreo

fun Window.setLightStatusBar(){
    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if ((context.applicationContext as HasImmersive).isEnabled()){
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    if (isMarshmallow() && !context.isDark()){
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

    if ((context.applicationContext as HasImmersive).isEnabled()){
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    if (isOreo() && !context.isDark()){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        navigationBarColor = Color.WHITE
    }
    decorView.systemUiVisibility = flags
}