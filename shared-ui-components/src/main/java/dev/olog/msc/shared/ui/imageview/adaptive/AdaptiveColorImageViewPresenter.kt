package dev.olog.msc.shared.ui.imageview.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import dev.olog.msc.shared.ui.extensions.colorPrimary
import dev.olog.msc.shared.ui.extensions.colorSurface
import dev.olog.msc.shared.ui.extensions.textColorPrimary
import dev.olog.msc.shared.ui.extensions.textColorSecondary
import dev.olog.msc.shared.ui.processor.ColorUtil
import dev.olog.msc.shared.ui.processor.ImageProcessor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

internal class AdaptiveColorImageViewPresenter(
    private val context: Context

) {

    private val defaultProcessorColors = ValidProcessorColors(
        context.colorSurface(), context.textColorPrimary(), context.textColorSecondary()
    )

    private var paletteJob: Job? = null
    private var processorJob: Job? = null

    private val defaultPaletteColors = ValidPaletteColors(context.colorPrimary())
    private val processorPaletteLiveData = MutableLiveData<ProcessorColors>()
    private val paletteLiveData = MutableLiveData<PaletteColors>()

    init {
        processorPaletteLiveData.value = defaultProcessorColors
        paletteLiveData.value = defaultPaletteColors
    }

    fun observeProcessorColors(): LiveData<ProcessorColors> = processorPaletteLiveData
    fun observePalette(): LiveData<PaletteColors> = paletteLiveData

    fun onNextImage(drawable: Drawable?) {
        try {
            onNextImage(drawable?.toBitmap(300, 300))
        } catch (ex: Exception) {
            if (ex !is IllegalArgumentException) {
                ex.printStackTrace()
            }
        }
    }

    fun onNextImage(bitmap: Bitmap?) {
        try {
            paletteJob?.cancel()
            processorJob?.cancel()

            processorJob = GlobalScope.launch {
                if (bitmap == null) {
                    processorPaletteLiveData.postValue(defaultProcessorColors)
                    return@launch
                }
                val image = ImageProcessor(context).processImage(bitmap)
                yield()
                processorPaletteLiveData.postValue(
                    ValidProcessorColors(
                        image.background,
                        image.primaryTextColor, image.secondaryTextColor
                    )
                )
            }

            paletteJob = GlobalScope.launch {
                if (bitmap == null) {
                    paletteLiveData.postValue(defaultPaletteColors)
                    return@launch
                }
                val image = Palette.from(bitmap).generate()
                yield()
                val accent = ColorUtil.getAccentColor(context, image)
                yield()
                paletteLiveData.postValue(ValidPaletteColors(accent))
            }
        } catch (ex: Exception) {
            if (ex !is IllegalArgumentException) {
                ex.printStackTrace()
            }
        }
    }

    fun onDetach() {
        paletteJob?.cancel()
        processorJob?.cancel()
    }

}