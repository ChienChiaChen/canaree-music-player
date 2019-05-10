package dev.olog.msc.presentation.base

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.presentation.base.widgets.image.view.QuickActionView
import dev.olog.msc.shared.ui.theme.ImageShape

object ImageViews {

    fun initialize(app: Application){
        updateIconShape(app)
    }

    var IMAGE_SHAPE = ImageShape.ROUND
    var QUICK_ACTION = QuickActionView.Type.NONE


    fun updateIconShape(context: Context){
        updateQuickAction(context)
        IMAGE_SHAPE = getIconShape(context)
    }

    fun updateQuickAction(context: Context){
        QUICK_ACTION = getQuickAction(context)
    }

    private fun getIconShape(context: Context): ImageShape {
        // TODO remove
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val shape = prefs.getString(context.getString(R.string.prefs_icon_shape_key), context.getString(R.string.prefs_icon_shape_rounded))!!
        return when (shape){
            context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
            context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
            else -> throw IllegalArgumentException("image shape not valid=$shape")
        }
    }

    private fun getQuickAction(context: Context): QuickActionView.Type {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val quickAction = preferences.getString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        return when (quickAction) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickActionView.Type.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickActionView.Type.PLAY
            else ->  QuickActionView.Type.SHUFFLE
        }
    }
}