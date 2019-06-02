package dev.olog.msc.presentation.base.interfaces

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

interface HasSlidingPanel {

    fun getSlidingPanel(): SuperCerealBottomSheetBehavior<*>

}

class SuperCerealBottomSheetBehavior<T : View> : BottomSheetBehavior<T>() {

    private val callbacks = mutableSetOf<BottomSheetCallback>()

    private val proxyCallback = object : BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            for (callback in callbacks) {
                callback.onSlide(bottomSheet, slideOffset)
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            for (callback in callbacks) {
                callback.onStateChanged(bottomSheet, newState)
            }
        }
    }

    fun addPanelSlideListener(callback: BottomSheetCallback) {
        if (callbacks.isEmpty()){
            setBottomSheetCallback(proxyCallback)
        }
        callbacks.add(callback)
    }

    fun removePanelSlideListener(callback: BottomSheetCallback) {
        callbacks.remove(callback)
        if (callbacks.isEmpty()){
            setBottomSheetCallback(null)
        }
    }

}