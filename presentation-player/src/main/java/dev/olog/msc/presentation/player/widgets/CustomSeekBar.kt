package dev.olog.msc.presentation.player.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class CustomSeekBar(context: Context, attrs: AttributeSet?) : AppCompatSeekBar(context, attrs) {

    private var isTouched = false

    private var listener: OnSeekBarChangeListener? = null

    fun setListener(onProgressChanged: (Int) -> Unit,
                    onStartTouch: (Int) -> Unit,
                    onStopTouch: (Int) -> Unit) {

        listener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTouched = true
                onStartTouch(progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTouched = false
                onStopTouch(progress)
            }
        }

        setOnSeekBarChangeListener(null) // clear old listener
        setOnSeekBarChangeListener(listener)
    }

    fun updateColor(color: Int){
        thumbTintList = ColorStateList.valueOf(color)
        progressTintList = ColorStateList.valueOf(color)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            setOnSeekBarChangeListener(listener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnSeekBarChangeListener(null)
    }

    override fun setProgress(progress: Int) {
        if (!isTouched || isInEditMode) {
            super.setProgress(progress)
        }
    }

    override fun setProgress(progress: Int, animate: Boolean) {
        if (!isTouched || isInEditMode) {
            super.setProgress(progress, animate)
        }
    }

}