package dev.olog.msc.presentation.player.widgets.shadow

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.Target
import dev.olog.msc.imageprovider.CoverUtils
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.base.ripple.RippleTarget
import dev.olog.msc.presentation.base.widgets.image.view.PlayerImageView
import dev.olog.msc.presentation.media.getMediaId
import dev.olog.msc.presentation.player.R
import dev.olog.msc.presentation.player.widgets.shadow.PlayerShadowImageView.Companion.DOWNSCALE_FACTOR
import dev.olog.msc.shared.extensions.dpToPx
import kotlin.properties.Delegates

class PlayerShadowImageView(context: Context, attrs: AttributeSet?) : PlayerImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 0.5f
        private const val DEFAULT_COLOR = -1
        private const val BRIGHTNESS = -25f
        private const val SATURATION = 1.3f
        private const val TOP_OFFSET = 2.2f
        private const val PADDING = 22f
        internal const val DOWNSCALE_FACTOR = 0.2f
    }


    var radiusOffset by Delegates.vetoable(DEFAULT_RADIUS) { _, _, newValue ->
        newValue > 0F || newValue <= 1
    }

    var shadowColor = DEFAULT_COLOR

    init {
        if (!isInEditMode) {
            BlurShadow.init(context.applicationContext)
            cropToPadding = false
            super.setScaleType(ScaleType.CENTER_CROP)
            val padding = context.dpToPx(PADDING)
            setPadding(padding, padding, padding, padding)
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowView, 0, 0)
            shadowColor = typedArray.getColor(R.styleable.ShadowView_shadowColor, DEFAULT_COLOR)
            radiusOffset = typedArray.getFloat(R.styleable.ShadowView_radiusOffset, DEFAULT_RADIUS)
            typedArray.recycle()
        }
    }

    override fun loadImage(metadata: MediaMetadataCompat) {
        val mediaId = metadata.getMediaId()

        GlideApp.with(context).clear(this)

        GlideApp.with(context)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .override(Target.SIZE_ORIGINAL)
                .into(RippleTarget(this))
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (!isInEditMode) {
            setBlurShadow { super.setImageDrawable(BitmapDrawable(resources, bm)) }
        } else {
            super.setImageBitmap(bm)
        }
    }

    override fun setImageResource(resId: Int) {
        if (!isInEditMode) {
            setBlurShadow { super.setImageDrawable(ContextCompat.getDrawable(context, resId)) }
        } else {
            super.setImageResource(resId)
        }
    }

    fun setImageResource(resId: Int, withShadow: Boolean) {
        if (withShadow) {
            setImageResource(resId)
        } else {
            background = null
            super.setImageResource(resId)
        }
    }

    fun setImageDrawable(drawable: Drawable?, withShadow: Boolean) {
        if (withShadow) {
            setImageDrawable(drawable)
        } else {
            background = null
            super.setImageDrawable(drawable)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (!isInEditMode) {
            setBlurShadow { super.setImageDrawable(drawable) }
        } else {
            super.setImageDrawable(drawable)
        }
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(ScaleType.CENTER_CROP)
    }

    private fun setBlurShadow(setImage: () -> Unit = {}) {
        background = null
        if (height != 0 || measuredHeight != 0) {
            setImage()
            makeBlurShadow()
        } else {
            val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    setImage()
                    makeBlurShadow()
                    return false
                }
            }
            viewTreeObserver.addOnPreDrawListener(preDrawListener)
        }
    }

    private fun makeBlurShadow() {
        var radius = resources.getInteger(R.integer.radius).toFloat()
        radius *= 2 * radiusOffset
        val blur = BlurShadow.blur(this, width, height - context.dpToPx(TOP_OFFSET), radius)
        //brightness -255..255 -25 is default
        val colorMatrix = ColorMatrix(
                floatArrayOf(
                        1f, 0f, 0f, 0f, BRIGHTNESS,
                        0f, 1f, 0f, 0f, BRIGHTNESS,
                        0f, 0f, 1f, 0f, BRIGHTNESS,
                        0f, 0f, 0f, 1f, 0f
                )
        ).apply { setSaturation(SATURATION) }

        background = BitmapDrawable(resources, blur).apply {
            this.colorFilter = ColorMatrixColorFilter(colorMatrix)
            applyShadowColor(this)
        }
        //super.setImageDrawable(null)
    }

    private fun applyShadowColor(bitmapDrawable: BitmapDrawable) {
        if (shadowColor != DEFAULT_COLOR) {
            bitmapDrawable.colorFilter = PorterDuffColorFilter(shadowColor, PorterDuff.Mode.SRC_IN)
        }
    }

}

object BlurShadow {

    private var renderScript: RenderScript? = null

    fun init(context: Context) {
        if (renderScript == null)
            renderScript = RenderScript.create(context)
    }

    fun blur(view: ImageView, width: Int, height: Int, radius: Float): Bitmap? {
        val src = getBitmapForView(view, DOWNSCALE_FACTOR, width, height)
                ?: return null
        val input = Allocation.createFromBitmap(renderScript, src)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(src)
        return src
    }

    private fun getBitmapForView(view: ImageView, downscaleFactor: Float, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(
                (width * downscaleFactor).toInt(),
                (height * downscaleFactor).toInt(),
                Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        matrix.preScale(downscaleFactor, downscaleFactor)
        canvas.matrix = matrix
        view.draw(canvas)
        return bitmap
    }
}