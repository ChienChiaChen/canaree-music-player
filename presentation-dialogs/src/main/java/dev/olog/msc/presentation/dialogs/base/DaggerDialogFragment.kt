package dev.olog.msc.presentation.dialogs.base

import android.content.Context
import androidx.fragment.app.DialogFragment
import dagger.android.support.AndroidSupportInjection

abstract class DaggerDialogFragment : DialogFragment(){

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

}