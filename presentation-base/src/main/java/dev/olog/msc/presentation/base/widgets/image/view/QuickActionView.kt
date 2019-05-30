package dev.olog.msc.presentation.base.widgets.image.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.ImageViews
import dev.olog.msc.presentation.base.R
import dev.olog.msc.presentation.media.MediaProvider
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import kotlin.properties.Delegates

class QuickActionView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : AppCompatImageView(context, attrs), View.OnClickListener {

    private var currentMediaId by Delegates.notNull<MediaId>()

    enum class Type {
        NONE, PLAY, SHUFFLE
    }

    init {
        setupImage()
        setBackgroundResource(R.drawable.background_quick_action)
    }

    private fun setupImage(){
        toggleVisibility(ImageViews.QUICK_ACTION != Type.NONE, true)

        when (ImageViews.QUICK_ACTION){
            Type.NONE -> setImageDrawable(null)
            Type.PLAY -> setImageResource(R.drawable.vd_play)
            Type.SHUFFLE -> setImageResource(R.drawable.vd_shuffle)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnClickListener(null)
    }

    fun setId(mediaId: MediaId){
        this.currentMediaId = mediaId
    }

    override fun onClick(v: View?) {
        val mediaProvider = context as MediaProvider
        when (ImageViews.QUICK_ACTION){
            Type.PLAY -> mediaProvider.playFromMediaId(currentMediaId)
            Type.SHUFFLE -> mediaProvider.shuffle(currentMediaId)
            else -> {}
        }
    }
}