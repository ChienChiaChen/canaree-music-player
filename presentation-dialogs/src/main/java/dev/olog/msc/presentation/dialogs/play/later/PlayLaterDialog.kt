package dev.olog.msc.presentation.dialogs.play.later

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.presentation.dialogs.base.BaseDialog
import dev.olog.msc.presentation.dialogs.play.later.di.inject
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.asHtml
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayLaterDialog : BaseDialog() {

    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(arguments!!.getString(Fragments.ARGUMENTS_MEDIA_ID)!!)
    }
    private val listSize: Int by lazyFast { arguments!!.getInt(Fragments.ARGUMENTS_ITEM_COUNT) }
    private val title: String by lazyFast { arguments!!.getString(Fragments.ARGUMENTS_TITLE) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<PlayLaterDialogViewModel>(factory) }

    override fun injectComponent() {
        inject()
    }

    override fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return builder.setTitle(R.string.popup_play_later)
                .setMessage(createMessage().asHtml())
                .setPositiveButton(R.string.common_ok, null)
                .setNegativeButton(R.string.common_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launch {
            var message: String
            try {
                val mediaController = MediaControllerCompat.getMediaController(activity!!)
                viewModel.executeAsync(mediaId, mediaController).await()
                message = successMessage(act)
            } catch (ex: Exception) {
                message = failMessage(act)
            }
            act.toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        return if (mediaId.isLeaf) {
            context.getString(R.string.song_x_added_to_play_later, title)
        } else context.resources.getQuantityString(R.plurals.xx_songs_added_to_play_later, listSize, listSize)
    }

    private  fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage(): String {
        if (mediaId.isAll || mediaId.isLeaf) {
            return getString(R.string.add_song_x_to_play_later, title)
        }
        return context!!.resources.getQuantityString(R.plurals.add_xx_songs_to_play_later, listSize, listSize)
    }

}