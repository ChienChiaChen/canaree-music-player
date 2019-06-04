package dev.olog.msc.shared.interfaces

import android.app.Activity
import android.view.View

interface IPopupFacade {
    fun main(activity: Activity, anchor: View, stringMediaIdCategory: String?)
    fun item(anchor: View, stringMediaId: String)
}