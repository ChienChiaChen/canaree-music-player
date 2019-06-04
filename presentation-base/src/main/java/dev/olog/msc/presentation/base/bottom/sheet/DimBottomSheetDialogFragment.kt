package dev.olog.msc.presentation.base.bottom.sheet

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class DimBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DimBottomSheetDialog(context!!, theme)
    }

}