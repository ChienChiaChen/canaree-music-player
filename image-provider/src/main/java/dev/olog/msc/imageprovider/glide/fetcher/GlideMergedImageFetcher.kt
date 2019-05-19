package dev.olog.msc.imageprovider.glide.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.core.gateway.track.GenreGateway
import dev.olog.msc.core.gateway.track.PlaylistGateway
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.imageprovider.creator.impl.MergedImagesCreator
import java.io.File
import java.io.InputStream

class GlideMergedImageFetcher(
    private val context: Context,
    private val mediaId: MediaId,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway
) : DataFetcher<InputStream> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val inputStream: InputStream?
        if (mediaId.isFolder) {
            inputStream = makeFolderImage(mediaId.categoryValue)
        } else if (mediaId.isGenre) {
            inputStream = makeGenreImage(mediaId.categoryId)
        } else {
            inputStream = makePlaylistImage(mediaId.categoryId)
        }
        callback.onDataReady(inputStream)
    }

    private fun makeFolderImage(folder: String): InputStream? {
//        val folderImage = ImagesFolderUtils.forFolder(context, dirPath) --contains current image
        val albumsId = folderGateway.getSongListByParam(folder).getAll(Filter.NO_FILTER)
            .map { it.albumId }

        val folderName = ImagesFolderUtils.FOLDER
        val normalizedPath = folder.replace(File.separator, "")

        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = normalizedPath
        )
        return file?.inputStream()
    }

    private fun makeGenreImage(genreId: Long): InputStream? {
//        ImagesFolderUtils.forGenre(context, id) --contains current image

        val albumsId = genreGateway.getSongListByParam(genreId).getAll(Filter.NO_FILTER)
            .map { it.albumId }

        val folderName = ImagesFolderUtils.GENRE
        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = "$genreId"
        )
        return file?.inputStream()
    }

    private fun makePlaylistImage(playlistId: Long): InputStream? {
//        ImagesFolderUtils.forPlaylist(context, id) --contains current image
        val albumsId = playlistGateway.getSongListByParam(playlistId).getAll(Filter.NO_FILTER)
            .map { it.albumId }

        val folderName = ImagesFolderUtils.PLAYLIST
        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = "$playlistId"
        )
        return file?.inputStream()
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cleanup() {

    }

    override fun cancel() {

    }

}