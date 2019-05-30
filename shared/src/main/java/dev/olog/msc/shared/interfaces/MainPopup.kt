package dev.olog.msc.shared.interfaces

import android.app.Activity
import android.view.View

/**
 * Workaround interface to call methods on main popup
 */
interface MainPopup {

    fun show(activity: Activity, anchor: View, mediaIdCategoryOrdinal: Int?)

}