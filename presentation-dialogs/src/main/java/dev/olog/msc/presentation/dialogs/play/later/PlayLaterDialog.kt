package dev.olog.msc.presentation.dialogs.play.later

import android.content.Context
import android.content.DialogInterface
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.dialogs.BaseDialog
import dev.olog.msc.presentation.base.extensions.asHtml
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.shared.extensions.lazyFast
import io.reactivex.Completable
import javax.inject.Inject

class PlayLaterDialog : BaseDialog() {

    companion object {
        const val TAG = "PlayLaterDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): PlayLaterDialog {
            return PlayLaterDialog().withArguments(
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
    private val viewModel by lazyFast { viewModelProvider<PlayLaterDialogViewModel>(factory) }

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_play_later)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.common_cancel
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.common_ok
    }

    override fun successMessage(context: Context): CharSequence {
        return if (mediaId.isLeaf) {
            context.getString(R.string.song_x_added_to_play_later, title)
        } else context.resources.getQuantityString(R.plurals.xx_songs_added_to_play_later, listSize, listSize)
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
        val mediaController = MediaControllerCompat.getMediaController(activity!!)
//        return viewModel.execute(mediaId, mediaController)
        return TODO()
    }

    private fun createMessage(): String {
        if (mediaId.isAll || mediaId.isLeaf) {
            return getString(R.string.add_song_x_to_play_later, title)
        }
        return context!!.resources.getQuantityString(R.plurals.add_xx_songs_to_play_later, listSize, listSize)
    }

}