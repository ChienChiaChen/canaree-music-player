package dev.olog.msc.presentation.dialogs.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.shared.ui.ThemedDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseDialog : DaggerDialogFragment(), CoroutineScope by MainScope() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var builder = ThemedDialog.builder(act)
        builder = extendBuilder(builder)

        val dialog = builder.show()
        extendDialog(dialog)

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            positionButtonAction(act)
        }

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
            negativeButtonAction(act)
        }
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            neutralButtonAction(act)
        }

        return dialog
    }

    protected abstract fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder
    protected open fun extendDialog(dialog: AlertDialog) {}

    protected open fun positionButtonAction(context: Context) {}
    protected open fun negativeButtonAction(context: Context) {}
    protected open fun neutralButtonAction(context: Context) {}

}