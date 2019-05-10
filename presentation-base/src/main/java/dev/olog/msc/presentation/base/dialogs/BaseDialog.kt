package dev.olog.msc.presentation.base.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.fragment.BaseDialogFragment
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.ThemedDialog
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class BaseDialog : BaseDialogFragment() {

    private var disposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val application = activity!!.application

        val builder = ThemedDialog.builder(act)
                .setTitle(title(application))
                .setMessage(message(application))
                .setNegativeButton(negativeButtonMessage(application), null)
                .setPositiveButton(positiveButtonMessage(application)) { dialog, which ->
                    disposable = positiveAction(dialog, which)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete { application.toast(successMessage(application)) }
                            .doOnError { application.toast(failMessage(application)) }
                            .subscribe({}, Throwable::printStackTrace)
                }

        return builder.show()
    }

    protected abstract fun title(context: Context): CharSequence
    protected abstract fun message(context: Context): CharSequence

    protected abstract fun negativeButtonMessage(context: Context): Int
    protected abstract fun positiveButtonMessage(context: Context): Int

    protected abstract fun successMessage(context: Context): CharSequence
    protected abstract fun failMessage(context: Context): CharSequence

    protected abstract fun positiveAction(dialogInterface: DialogInterface, which: Int) : Completable

}