package dev.olog.msc.presentation.player.animation

import android.view.View

fun View.rotate(degrees: Int){
    animate().cancel()
    animate().rotation(degrees.toFloat())
        .setDuration(200)
        .withEndAction { animate().rotation(0f).setDuration(200) }
}