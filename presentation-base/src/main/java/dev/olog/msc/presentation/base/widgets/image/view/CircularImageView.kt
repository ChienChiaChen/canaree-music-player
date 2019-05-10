package dev.olog.msc.presentation.base.widgets.image.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import dev.olog.msc.shared.ui.R
import dev.olog.msc.shared.ui.imageview.ForegroundImageView

class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circular_corners_drawable)
        clipToOutline = true
    }

}