package dev.olog.msc.presentation.dialogs.ringtone

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.presentation.dialogs.R
import dev.olog.msc.shared.ui.ThemedDialog
import dev.olog.msc.shared.utils.isMarshmallow
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class SetRingtoneDialogViewModel @Inject constructor(
        @ApplicationContext private val application: Context

) : ViewModel() {

    fun executeAsync(activity: FragmentActivity, mediaId: MediaId) = viewModelScope.async {
        if (!isMarshmallow() || (isMarshmallow()) && Settings.System.canWrite(application)) {
            setRingtone(mediaId)
        } else {
            requestWritingSettingsPermission(activity)
        }
    }

    @TargetApi(23)
    private fun requestWritingSettingsPermission(activity: FragmentActivity) {
        ThemedDialog.builder(activity)
                .setTitle(R.string.popup_permission)
                .setMessage(R.string.popup_request_permission_write_settings)
                .setNegativeButton(R.string.common_cancel, null)
                .setPositiveButton(R.string.common_ok) { _, _ ->
                    val packageName = application.packageName
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
                    activity.startActivity(intent)
                }.show()
    }

    private fun setRingtone(mediaId: MediaId): Boolean {
        val songId = mediaId.leaf!!
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val values = ContentValues(1)
        values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, "1")

        application.contentResolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values, "${BaseColumns._ID} = ?", arrayOf("$songId"))

        return Settings.System.putString(application.contentResolver, Settings.System.RINGTONE, uri.toString())
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}