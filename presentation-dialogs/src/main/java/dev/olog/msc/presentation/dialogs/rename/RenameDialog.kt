package dev.olog.msc.presentation.dialogs.rename

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.presentation.dialogs.base.BaseEditTextDialog
import dev.olog.msc.presentation.dialogs.rename.di.inject
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.toast
import javax.inject.Inject

class RenameDialog : BaseEditTextDialog() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<RenameDialogViewModel>(factory) }
    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(arguments!!.getString(Fragments.ARGUMENTS_MEDIA_ID)!!)
    }
    private val title: String by lazyFast { arguments!!.getString(Fragments.ARGUMENTS_TITLE) }

    override fun injectComponent() {
        inject()
    }

    override fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return super.extendBuilder(builder)
                .setTitle(R.string.popup_rename)
                .setPositiveButton(R.string.popup_positive_rename, null)
                .setNegativeButton(R.string.common_cancel, null)
    }

    override fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {
        editText.setText(arguments!!.getString(Fragments.ARGUMENTS_TITLE))
    }

    override fun provideMessageForBlank(): String {
        return getString(R.string.popup_playlist_name_not_valid)
    }

    override suspend fun onItemValid(string: String) {
        var message: String
        try {
            viewModel.executeAsync(mediaId, string).await()
            message = successMessage(act, string).toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
            message = getString(R.string.popup_error_message)
        }
        act.toast(message)
    }

    private fun successMessage(context: Context, currentValue: String): CharSequence {
        return context.getString(R.string.playlist_x_renamed_to_y, title, currentValue)
    }

}