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
import dev.olog.msc.core.entity.Album
import dev.olog.msc.core.entity.Folder
import dev.olog.msc.core.entity.Genre
import dev.olog.msc.core.entity.Playlist
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.interactor.all.sibling.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable

@Module
class DetailFragmentModuleAlbum {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderData(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetFolderSiblingsUseCase
    ): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .mapToList { it.toDetailDisplayableItem(context.resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistData(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetPlaylistSiblingsUseCase
    ): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .mapToList { it.toDetailDisplayableItem(context.resources) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumData(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetAlbumSiblingsByAlbumUseCase
    ): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .mapToList { it.toDetailDisplayableItem(context.resources) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistData(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetAlbumSiblingsByArtistUseCase
    ): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .mapToList { it.toDetailDisplayableItem(context.resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreData(
        @ApplicationContext context: Context,
        mediaId: MediaId,
        useCase: GetGenreSiblingsUseCase
    ): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
            .mapToList { it.toDetailDisplayableItem(context.resources) }
    }


}

private fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.folderId(path),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

private fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.playlistId(id),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

private fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.albumId(id),
        title,
        resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase()
    )
}

private fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
        R.layout.item_detail_album,
        MediaId.genreId(id),
        name,
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}