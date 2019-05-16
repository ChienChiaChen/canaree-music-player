package dev.olog.msc.presentation.dialogs.playlist

import android.content.Context
import android.content.DialogInterface
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

class ClearPlaylistDialog : BaseDialog() {

    companion object {
        const val TAG = "ClearPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, itemTitle: String): ClearPlaylistDialog {
            return ClearPlaylistDialog().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString(),
                ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val title by lazy { arguments!!.getString(ARGUMENTS_ITEM_TITLE) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<ClearPlaylistDialogViewModel>(factory) }

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_clear_playlist)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.common_cancel
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.common_delete
    }

    override fun successMessage(context: Context): CharSequence {
        return context.getString(R.string.playlist_x_cleared, title)
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
        val mediaId = MediaId.fromString(arguments!!.getString(ARGUMENTS_MEDIA_ID)!!)
//        return viewModel.execute(mediaId)
        TODO()
    }

    private fun createMessage(): String {
        return context!!.getString(R.string.remove_songs_from_playlist_y, title)
    }

}