package dev.olog.msc.shared.ui.playpause

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AnimatedPlayPauseImageViewFab : FloatingActionButton, IPlayPauseBehavior {

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs, com.google.android.material.R.attr.floatingActionButtonStyle)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val behavior = PlayPauseBehaviorImpl(this)

    override fun animationPlay(animate: Boolean) {
        behavior.animationPlay(animate)
    }

    override fun animationPause(animate: Boolean) {
        behavior.animationPause(animate)
    }

}

