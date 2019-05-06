package dev.olog.msc.shared.ui.imageview

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import dev.olog.msc.shared.ui.R

class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circular_corners_drawable)
        clipToOutline = true
    }

}