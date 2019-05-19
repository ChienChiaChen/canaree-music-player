package dev.olog.msc.data.repository

import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.UsedAlbumImageEntity
import dev.olog.msc.data.entity.UsedArtistImageEntity
import dev.olog.msc.data.entity.UsedTrackImageEntity
import dev.olog.msc.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal class UsedImageRepository @Inject constructor(
    appDatabase: AppDatabase

) : UsedImageGateway {

    private val dao = appDatabase.usedImageDao()

    override fun getForTrack(id: Long): String? {
        assertBackgroundThread()
        return dao.getImageForTrack(id)
    }

    override fun getForAlbum(id: Long): String? {
        assertBackgroundThread()
        return dao.getImageForAlbum(id)
    }

    override fun getForArtist(id: Long): String? {
        assertBackgroundThread()
        return dao.getImageForArtist(id)
    }

    override suspend fun setForTrack(id: Long, image: String?) {
        assertBackgroundThread()
        if (image == null) {
            dao.deleteForTrack(id)
        } else {
            dao.insertForTrack(UsedTrackImageEntity(id, image))
        }
    }

    override suspend fun setForAlbum(id: Long, image: String?) {
        assertBackgroundThread()
        if (image == null) {
            dao.deleteForAlbum(id)
        } else {
            dao.insertForAlbum(UsedAlbumImageEntity(id, image))
        }
    }

    override suspend fun setForArtist(id: Long, image: String?) {
        assertBackgroundThread()
        if (image == null) {
            dao.deleteForArtist(id)
        } else {
            dao.insertForArtist(UsedArtistImageEntity(id, image))
        }
    }
}