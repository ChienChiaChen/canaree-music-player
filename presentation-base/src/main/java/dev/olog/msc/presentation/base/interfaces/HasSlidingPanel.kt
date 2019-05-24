package dev.olog.msc.presentation.base.interfaces

import com.sothree.slidinguppanel.SlidingUpPanelLayout

interface HasSlidingPanel {

    fun getSlidingPanel(): SlidingUpPanelLayout

    fun addPanelSlideListener(listener: SlidingUpPanelLayout.PanelSlideListener){
        getSlidingPanel().addPanelSlideListener(listener)
    }

    fun removePanelSlideListener(listener: SlidingUpPanelLayout.PanelSlideListener){
        getSlidingPanel().removePanelSlideListener(listener)
    }

}
