package dev.olog.msc.presentation.dialogs.play.next

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.asHtml
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.presentation.dialogs.base.BaseDialog
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.extensions.toast
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayNextDialog : BaseDialog() {

    companion object {
        const val TAG = "PlayNextDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): PlayNextDialog {
            return PlayNextDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(arguments!!.getString(ARGUMENTS_MEDIA_ID)!!)
    }
    private val listSize: Int by lazyFast { arguments!!.getInt(ARGUMENTS_LIST_SIZE) }
    private val title: String by lazyFast { arguments!!.getString(ARGUMENTS_ITEM_TITLE) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<PlayNextDialogViewModel>(factory) }

    override fun extendBuilder(builder: AlertDialog.Builder): AlertDialog.Builder {
        return builder.setTitle(R.string.popup_play_next)
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
            context.getString(R.string.song_x_added_to_play_next, title)
        } else context.resources.getQuantityString(R.plurals.xx_songs_added_to_play_next, listSize, listSize)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage(): String {
        if (mediaId.isAll || mediaId.isLeaf) {
            return getString(R.string.add_song_x_to_play_next, title)
        }
        return context!!.resources.getQuantityString(R.plurals.add_xx_songs_to_play_next, listSize, listSize)
    }

}