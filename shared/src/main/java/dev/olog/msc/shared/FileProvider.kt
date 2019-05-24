package dev.olog.msc.shared

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FileProvider {

    private const val authority = "dev.olog.msc.fileprovider"

    fun getUriForPath(context: Context, path: String): Uri {
        return try {
            FileProvider.getUriForFile(context, authority, File(path))
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Uri.EMPTY
        }
    }

}