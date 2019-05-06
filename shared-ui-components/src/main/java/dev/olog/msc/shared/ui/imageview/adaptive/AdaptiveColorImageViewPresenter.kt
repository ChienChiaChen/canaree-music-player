package dev.olog.msc.shared.ui.imageview.adaptive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.msc.shared.ui.extensions.colorAccent
import dev.olog.msc.shared.ui.extensions.textColorPrimary
import dev.olog.msc.shared.ui.extensions.textColorSecondary
import dev.olog.msc.shared.ui.extensions.windowBackground
import dev.olog.msc.shared.ui.processor.ColorUtil
import dev.olog.msc.shared.ui.processor.ImageProcessor
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

internal class AdaptiveColorImageViewPresenter(
        private val context: Context

) {

    private val defaultProcessorColors = ValidProcessorColors(
            context.windowBackground(), context.textColorPrimary(), context.textColorSecondary())

    private val defaultPaletteColors = ValidPaletteColors(context.colorAccent())

    private val processorPalettePublisher = BehaviorSubject.createDefault<ProcessorColors>(defaultProcessorColors)
    private val palettePublisher = BehaviorSubject.createDefault<PaletteColors>(defaultPaletteColors)

    private var processorDisposable: Disposable? = null
    private var paletteDisposable: Disposable? = null

    fun observeProcessorColors() = processorPalettePublisher
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .debounceFirst()

    fun observePalette() = palettePublisher
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .debounceFirst()

    fun onNextImage(drawable: Drawable?){
        try {
            onNextImage(drawable?.toBitmap(300, 300))
        } catch (ex: Exception) {
            if (ex !is IllegalArgumentException){
                ex.printStackTrace()
            }
        }
    }

    fun onNextImage(bitmap: Bitmap?){
        try {
            processorDisposable.unsubscribe()
            paletteDisposable.unsubscribe()

            if (bitmap == null){
                processorPalettePublisher.onNext(defaultProcessorColors)
                palettePublisher.onNext(defaultPaletteColors)
                return
            }

            processorDisposable = Single.fromCallable { ImageProcessor(context).processImage(bitmap) }
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        processorPalettePublisher.onNext(ValidProcessorColors(it.background,
                                it.primaryTextColor, it.secondaryTextColor))
                    }, Throwable::printStackTrace)

            paletteDisposable = Single.fromCallable { Palette.from(bitmap).generate() }
                    .map { ColorUtil.getAccentColor(context, it) }
                    .subscribeOn(Schedulers.computation())
                    .subscribe({
                        palettePublisher.onNext(ValidPaletteColors(it))
                    }, Throwable::printStackTrace)
        } catch (ex: Exception){
            if (ex !is IllegalArgumentException){
                ex.printStackTrace()
            }
        }
    }

}