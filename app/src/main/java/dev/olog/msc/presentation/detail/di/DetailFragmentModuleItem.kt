package dev.olog.msc.presentation.detail.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.dagger.ApplicationContext
import dev.olog.msc.core.entity.*
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.interactor.item.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.asFlowable
import io.reactivex.Flowable

@Module
class DetailFragmentModuleItem {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderItem(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetFolderUseCase
    ): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .map { it.toHeaderItem(context.resources) }
            .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistItem(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetPlaylistUseCase
    ): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .map { it.toHeaderItem(context.resources) }
            .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumItem(
        mediaId: MediaId,
        useCase: GetAlbumUseCase
    ): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .map { it.toHeaderItem() }
            .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistItem(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetArtistUseCase
    ): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .map { it.toHeaderItem(context.resources) }
            .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreItem(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetGenreUseCase
    ): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .map { it.toHeaderItem(context.resources) }
            .asFlowable()
    }

}


private fun Folder.toHeaderItem(resources: Resources): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.folderId(path),
            title,
            subtitle = resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
        )
    )
}

private fun Playlist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val listSize = if (this.size == -1) {
        ""
    } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.playlistId(this.id),
            title,
            listSize
        )
    )

}

private fun Album.toHeaderItem(): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.albumId(this.id),
            title,
            DisplayableItem.adjustArtist(this.artist)
        )
    )
}

private fun Artist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(
            R.plurals.common_plurals_album,
            this.albums,
            this.albums
        )}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.artistId(this.id),
            name,
            "$albums$songs".toLowerCase()
        )
    )
}

private fun Genre.toHeaderItem(resources: Resources): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            MediaId.genreId(this.id),
            name,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
        )
    )
}