package dev.olog.msc.presentation.dialogs.delete

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.dialogs.BaseDialog
import dev.olog.msc.presentation.base.extensions.asHtml
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.shared.extensions.lazyFast
import io.reactivex.Completable
import javax.inject.Inject

class DeleteDialog : BaseDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString(),
                ARGUMENTS_LIST_SIZE to listSize,
                ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId: MediaId by lazyFast {
        val mediaId = arguments!!.getString(ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { arguments!!.getString(ARGUMENTS_ITEM_TITLE)!! }
    private val listSize: Int by lazyFast { arguments!!.getInt(ARGUMENTS_LIST_SIZE) }


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<DeleteDialogViewModel>(factory) }

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_delete)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.common_no
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.common_delete
    }

    override fun successMessage(context: Context): CharSequence {
        return when (mediaId.category) {
            MediaIdCategory.PLAYLISTS -> context.getString(R.string.playlist_x_deleted, title)
            MediaIdCategory.SONGS -> context.getString(R.string.song_x_deleted, title)
            else -> context.resources.getQuantityString(
                R.plurals.xx_songs_deleted_from_y,
                listSize, listSize, title
            )
        }
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
//        return viewModel.execute(mediaId)
        return TODO()
    }

    private fun createMessage(): String {
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)

        return when {
            mediaId.isAll || mediaId.isLeaf -> getString(R.string.delete_song_y, itemTitle)
            mediaId.isPlaylist -> getString(R.string.delete_playlist_y, itemTitle)
            else -> context!!.resources.getQuantityString(R.plurals.delete_xx_songs_from_y, listSize, listSize)
        }
    }

}