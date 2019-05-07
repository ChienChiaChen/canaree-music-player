package dev.olog.msc.presentation.dialog.set.ringtone

import android.annotation.TargetApi
import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import dev.olog.msc.R

import dev.olog.msc.core.MediaId
import dev.olog.msc.shared.ui.ThemedDialog
import dev.olog.msc.shared.utils.isMarshmallow
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SetRingtoneDialogPresenter @Inject constructor(
        private val application: Application,
        private val activity: AppCompatActivity,
        private val mediaId: MediaId

) {

    @TargetApi(Build.VERSION_CODES.M)
    fun execute() : Completable {
        if (!isMarshmallow() || (isMarshmallow()) && Settings.System.canWrite(application)){
            return setRingtone()
        } else {
            requestWritingSettingsPermission()
            return Completable.never()
        }
    }

    private fun setRingtone(): Completable{
        return Completable.fromCallable(this::writeSettings)
                .subscribeOn(Schedulers.io())
    }

    @TargetApi(23)
    private fun requestWritingSettingsPermission(){
        ThemedDialog.builder(activity)
                .setTitle(R.string.popup_permission)
                .setMessage(R.string.popup_request_permission_write_settings)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    val packageName = application.packageName
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
                    activity.startActivity(intent)
                }).show()
    }

    private fun writeSettings() : Boolean {
        val songId = mediaId.leaf!!
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val values = ContentValues(2)
        values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1")

        application.contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values, "${BaseColumns._ID} = ?", arrayOf("$songId"))

        return Settings.System.putString(application.contentResolver, Settings.System.RINGTONE, uri.toString())
    }

}