package dev.olog.msc.imageprovider.creator

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore

internal object CommonQuery {
    fun extractAlbumIdsFromSongs(contentResolver: ContentResolver, uri: Uri): List<Long> {
        val result = mutableListOf<Long>()

        val projection = arrayOf(MediaStore.Audio.AudioColumns.ALBUM_ID)

        val cursor = contentResolver.query(uri, projection, null,
                null, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                result.add(it.getLong(0))
            }
        }

        return result
    }
}