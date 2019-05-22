package dev.olog.msc.shared.ui.imageview.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.extensions.layers
import dev.olog.msc.shared.ui.imageview.ForegroundImageView

open class AdaptiveColorImageView(context: Context, attrs: AttributeSet?) : ForegroundImageView(context, attrs) {

    private val presenter by lazyFast { AdaptiveColorImageViewPresenter(context) }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        presenter.onNextImage(bm)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable is TransitionDrawable) {
            if (drawable.numberOfLayers == 2) {
                presenter.onNextImage(drawable.layers[1])
            } else {
                presenter.onNextImage(drawable)
            }

        } else {
            presenter.onNextImage(drawable)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.onDetach()
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

}