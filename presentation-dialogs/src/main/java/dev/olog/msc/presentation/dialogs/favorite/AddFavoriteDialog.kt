package dev.olog.msc.presentation.dialogs.favorite


import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.presentation.dialogs.base.BaseDialog
import dev.olog.msc.presentation.dialogs.favorite.di.inject
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.asHtml
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddFavoriteDialog : BaseDialog() {

    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(arguments!!.getString(Fragments.ARGUMENTS_MEDIA_ID)!!)
    }
    private val title by lazyFast { arguments!!.getString(Fragments.ARGUMENTS_TITLE) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<AddFavoriteDialogViewModel>(factory) }

    override fun injectComponent() {
        inject()
    }

    override fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return builder.setTitle(R.string.popup_add_to_favorites)
                .setMessage(createMessage().asHtml())
                .setPositiveButton(R.string.common_ok, null)
                .setNegativeButton(R.string.common_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launch {
            var message: String
            try {
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
        return context.getString(R.string.song_x_added_to_favorites, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage(): String {
        return getString(R.string.add_song_x_to_favorite, title)
    }

}