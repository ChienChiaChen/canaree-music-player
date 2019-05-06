package dev.olog.msc.presentation.tutorial

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import dev.olog.msc.R
import dev.olog.msc.shared.ui.extensions.colorAccent
import dev.olog.msc.taptargetview.TapTarget
import dev.olog.msc.taptargetview.TapTargetSequence
import dev.olog.msc.taptargetview.TapTargetView

object TutorialTapTarget {

    fun sortBy(text: View, arrow: View){
        val context = text.context

        val textTarget = TapTarget.forView(text, context.getString(R.string.tutorial_sort_by_text))
                .transparentTarget(true)
                .tint(context)

        val arrowTarget = TapTarget.forView(arrow, context.getString(R.string.tutorial_sort_by_arrow))
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_arrow_down))
                .tint(context)

        TapTargetSequence(text.context as Activity)
                .targets(textTarget, arrowTarget)
                .start()

    }

    fun floatingWindow(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_floating_window))
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_search_text))
                .tint(context)
        TapTargetView.showFor(view.context as Activity, target)
    }

    fun lyrics(view: View){
        val context = view.context

        val target = TapTarget.forView(view, context.getString(R.string.tutorial_lyrics))
                .tint(context)
                .icon(ContextCompat.getDrawable(context, R.drawable.vd_offline_lyrics))

        TapTargetView.showFor(view.context as Activity, target)
    }

    private fun TapTarget.tint(context: Context): TapTarget {
        val accentColor = context.colorAccent()
        val backgroundColor = context.windowBackground()

        return this.tintTarget(true)
                .outerCircleColorInt(accentColor)
                .targetCircleColorInt(backgroundColor)
    }

}