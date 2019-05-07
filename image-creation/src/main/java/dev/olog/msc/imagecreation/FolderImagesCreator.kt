package dev.olog.msc.imagecreation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.imagecreation.domain.GetAllSongsNewRequestUseCase
import dev.olog.msc.imagecreation.impl.MergedImagesCreator
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.shared.utils.assertBackgroundThread
import io.reactivex.Flowable
import java.io.File
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

internal class FolderImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val getAllSongsUseCase: GetAllSongsNewRequestUseCase,
        private val imagesThreadPool: ImagesThreadPool

) {

    fun execute() : Flowable<*> {
        return getAllSongsUseCase.execute()
                .firstOrError()
                .observeOn(imagesThreadPool.scheduler)
                .map { it.groupBy { it.folderPath } }
                .flattenAsFlowable { it.entries }
                .parallel()
                .runOn(imagesThreadPool.scheduler)
                .map { entry -> try {
                    makeImage(entry)
                } catch (ex: Exception){ false }
                }
                .sequential()
                .buffer(10)
                .filter { it.reduce { acc, curr -> acc || curr } }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }


    private fun makeImage(map: Map.Entry<String, List<Song>>) : Boolean {
        assertBackgroundThread()
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.FOLDER)
        val normalizedPath = map.key.replace(File.separator, "")
        return MergedImagesCreator.makeImages(ctx, map.value.map { it.albumId },
                folderName, normalizedPath)
    }

}