package dev.olog.msc.presentation.base.bottom.sheet

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.olog.msc.presentation.base.R

class DimBottomSheetDialog(context: Context, theme: Int)
    : BottomSheetDialog(context, theme) {

    private fun getScrimColor(): Int {
        // TODO check if can do with new material components
        return 0x88FFFFFF.toInt()
//        if (context.isWhite()) { // TODO SET color in res
//            return 0x88FFFFFF.toInt()
//        }
//        return 0xAA232323.toInt()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        window?.findViewById<View>(R.id.container)?.setBackgroundColor(getScrimColor())
        window?.findViewById<View>(R.id.design_bottom_sheet)?.background = null
    }

}