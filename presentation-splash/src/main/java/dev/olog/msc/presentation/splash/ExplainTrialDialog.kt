package dev.olog.msc.presentation.splash

import android.content.Context
import androidx.appcompat.app.AlertDialog

object ExplainTrialDialog {

    fun show(context: Context, positiveAction: () -> Unit){
        AlertDialog.Builder(context)
                .setTitle(R.string.trial_title)
                .setMessage(R.string.trial_message)
                .setPositiveButton(R.string.trial_positive_button, { _, _ -> positiveAction() })
                .show()
    }

}