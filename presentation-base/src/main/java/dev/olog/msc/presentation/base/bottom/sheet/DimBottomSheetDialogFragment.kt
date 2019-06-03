package dev.olog.msc.presentation.base.bottom.sheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class DimBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onAttach(context: Context) {
        injectComponents()
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DimBottomSheetDialog(context!!, theme)
    }

    protected open fun injectComponents() {}

}