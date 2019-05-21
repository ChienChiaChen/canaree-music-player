package dev.olog.msc.presentation.base.bottom.sheet

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.olog.msc.presentation.base.R
import dev.olog.msc.presentation.base.theme.dark.mode.isWhite
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.extensions.isLandscape

class DimBottomSheetDialog(context: Context, theme: Int)
    : BottomSheetDialog(context, theme) {

//    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    private fun getScrimColor(): Int {
        if (context.isWhite()) {
            return 0x88FFFFFF.toInt()
        }
        return 0xAA232323.toInt()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        window?.findViewById<View>(R.id.container)?.setBackgroundColor(getScrimColor())
        window?.findViewById<View>(R.id.design_bottom_sheet)?.background = null

        if (context.isLandscape) {
//            val bottomSheet = window?.findViewById(R.id.design_bottom_sheet) as FrameLayout
//            behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = context.dip(300) // TODO check
        }
    }

}