package dev.olog.msc.presentation.dialogs.duplicates

import android.content.Context
import android.content.DialogInterface
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.dialogs.BaseDialog
import dev.olog.msc.presentation.base.extensions.asHtml
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.dialogs.R
import io.reactivex.Completable
import javax.inject.Inject

class RemoveDuplicatesDialog: BaseDialog() {

    companion object {
        const val TAG = "RemoveDuplicatesDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, itemTitle: String): RemoveDuplicatesDialog {
            return RemoveDuplicatesDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var title: String
    @Inject lateinit var presenter: RemoveDuplicatesDialogPresenter

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.remove_duplicates_title)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.common_no
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.common_remove
    }

    override fun successMessage(context: Context): CharSequence {
        return context.getString(R.string.remove_duplicates_success, title)
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
        return presenter.execute()
    }

    private fun createMessage() : String {
        return context!!.getString(R.string.remove_duplicates_message, title)
    }

}