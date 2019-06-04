package dev.olog.msc.presentation.dialogs.ringtone

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.presentation.dialogs.base.BaseDialog
import dev.olog.msc.presentation.dialogs.ringtone.di.inject
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.TrackUtils
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.asHtml
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetRingtoneDialog : BaseDialog() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<SetRingtoneDialogViewModel>(factory) }

    override fun injectComponent() {
        inject()
    }

    override fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return builder.setTitle(R.string.popup_set_as_ringtone)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.common_ok, null)
            .setNegativeButton(R.string.common_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launch {
            var message: String
            try {
                val mediaId = MediaId.fromString(arguments!!.getString(Fragments.ARGUMENTS_MEDIA_ID)!!)
                viewModel.executeAsync(act, mediaId).await()
                message = successMessage(act)
            } catch (ex: Exception) {
                message = failMessage(act)
            }
            act.toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        val title = generateItemDescription()
        return context.getString(R.string.song_x_set_as_ringtone, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage(): String {
        val title = generateItemDescription()
        return context!!.getString(R.string.song_x_will_be_set_as_ringtone, title)
    }

    private fun generateItemDescription(): String {
        var title = arguments!!.getString(Fragments.ARGUMENTS_TITLE)!!
        val artist = arguments!!.getString(Fragments.ARGUMENTS_ARTIST)
        if (artist != TrackUtils.UNKNOWN_ARTIST) {
            title += " $artist"
        }
        return title
    }


}