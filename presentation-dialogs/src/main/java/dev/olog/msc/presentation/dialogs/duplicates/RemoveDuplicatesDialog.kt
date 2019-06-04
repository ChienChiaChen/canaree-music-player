package dev.olog.msc.presentation.dialogs.duplicates

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.presentation.dialogs.base.BaseDialog
import dev.olog.msc.presentation.dialogs.duplicates.di.inject
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.asHtml
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.launch
import javax.inject.Inject

class RemoveDuplicatesDialog : BaseDialog() {

    private val title: String by lazyFast { arguments!!.getString(Fragments.ARGUMENTS_TITLE) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<RemoveDuplicatesDialogViewModel>(
            factory
        )
    }

    override fun injectComponent() {
        inject()
    }

    override fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return builder.setTitle(R.string.remove_duplicates_title)
                .setMessage(createMessage().asHtml())
                .setPositiveButton(R.string.common_remove, null)
                .setNegativeButton(R.string.common_no, null)
    }

    override fun positionButtonAction(context: Context) {
        launch {
            var message: String
            try {
                val mediaId = MediaId.fromString(arguments!!.getString(Fragments.ARGUMENTS_MEDIA_ID)!!)
                viewModel.executeAsync(mediaId).await()
                message = successMessage(act)
            } catch (ex: Exception) {
                message = failMessage(act)
            }
            act.toast(message)
            dismiss()

        }
    }


    private fun successMessage(context: Context): String {
        return context.getString(R.string.remove_duplicates_success, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage(): String {
        return context!!.getString(R.string.remove_duplicates_message, title)
    }

}