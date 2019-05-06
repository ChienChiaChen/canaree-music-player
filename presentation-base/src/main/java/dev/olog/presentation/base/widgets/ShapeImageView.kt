package dev.olog.presentation.base.widgets

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dev.olog.msc.shared.extensions.dipf
import dev.olog.msc.shared.ui.imageview.ForegroundImageView
import dev.olog.msc.shared.ui.theme.ImageShape
import dev.olog.presentation.base.ImageViews
import dev.olog.presentation.base.R

private const val DEFAULT_RADIUS = 5

private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

class ShapeImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    private val radius : Int
    private var mask : Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornersImageView)
        radius = a.getInt(R.styleable.RoundedCornersImageView_imageViewCornerRadius, DEFAULT_RADIUS)
        a.recycle()

        clipToOutline = true

        paint.xfermode = X_FERMO_MODE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        getMask()?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
    }

    private fun getMask(): Bitmap? {
        if (mask == null){
            mask = when (ImageViews.IMAGE_SHAPE){
                ImageShape.ROUND -> {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner)!! as GradientDrawable
                    drawable.cornerRadius = context.dipf(radius)
                    drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
                }
                ImageShape.RECTANGLE -> {
                    setLayerType(View.LAYER_TYPE_NONE, null)
                    null
                }
            }
        }
        return mask
    }

}