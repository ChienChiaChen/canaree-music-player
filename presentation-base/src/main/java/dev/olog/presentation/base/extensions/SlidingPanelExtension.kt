@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.presentation.base.extensions

import com.sothree.slidinguppanel.SlidingUpPanelLayout


inline fun SlidingUpPanelLayout?.isCollapsed() = this != null &&
        panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
inline fun SlidingUpPanelLayout?.isExpanded() = this != null &&
        panelState != SlidingUpPanelLayout.PanelState.COLLAPSED

inline fun SlidingUpPanelLayout?.collapse() {
    if (this != null){
        panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }
}

inline fun SlidingUpPanelLayout?.expand() {
    if (this != null){
        panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }
}