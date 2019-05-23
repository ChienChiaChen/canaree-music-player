package dev.olog.msc.presentation.detail

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import dev.olog.msc.shared.ui.extensions.colorPrimary
import dev.olog.msc.shared.ui.extensions.colorSurface
import dev.olog.msc.taptargetview.TapTarget
import dev.olog.msc.taptargetview.TapTargetSequence

object Tutorial {

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

    private fun TapTarget.tint(context: Context): TapTarget {
        val accentColor = context.colorPrimary()
        val backgroundColor = context.colorSurface()

        return this.tintTarget(true)
                .outerCircleColorInt(accentColor)
                .targetCircleColorInt(backgroundColor)
    }

}