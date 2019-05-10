package dev.olog.msc.presentation.dialogs.ringtone

import android.content.Context
import android.content.DialogInterface
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.dialogs.BaseDialog
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.asHtml
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.shared.TrackUtils
import io.reactivex.Completable
import javax.inject.Inject

class SetRingtoneDialog : BaseDialog() {

    companion object {
        const val TAG = "SetRingtoneDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_TITLE = "$TAG.arguments.title"
        const val ARGUMENTS_ARTIST = "$TAG.arguments.artist"

        fun newInstance(mediaId: MediaId, title: String, artist: String): SetRingtoneDialog {
            return SetRingtoneDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_TITLE to title,
                    ARGUMENTS_ARTIST to artist
            )
        }
    }

    @Inject lateinit var presenter: SetRingtoneDialogPresenter

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_set_as_ringtone)
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
        val title = generateItemDescription()
        return context.getString(R.string.song_x_set_as_ringtone, title)
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
        return presenter.execute(act)
    }

    private fun createMessage() : String{
        val title = generateItemDescription()
        return context!!.getString(R.string.song_x_will_be_set_as_ringtone, title)
    }

    private fun generateItemDescription(): String{
        var title = arguments!!.getString(ARGUMENTS_TITLE)!!
        val artist = arguments!!.getString(ARGUMENTS_ARTIST)
        if (artist != TrackUtils.UNKNOWN){
            title += " $artist"
        }
        return title
    }


}