package dev.olog.presentation.base.animation

import android.content.Context
import android.view.View
import dev.olog.presentation.base.R

class ScaleInOnTouch (
        private val view: View

) : AnimateOnTouch() {

    override fun animate(context: Context) {
        setAnimationAndPlay(view, R.animator.scale_in)
    }

    override fun restore(context: Context) {
        setAnimationAndPlay(view, R.animator.restore)
    }
}