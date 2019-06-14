package dev.olog.msc.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.msc.core.entity.Artist
import dev.olog.msc.core.entity.Song
import dev.olog.msc.core.gateway.ArtistGateway
import dev.olog.msc.core.gateway.SongGateway
import dev.olog.msc.data.dao.AppDatabase
import dev.olog.msc.data.mapper.toArtist
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistRepository @Inject constructor(
    private val rxContentResolver: BriteContentResolver,
    private val songGateway: SongGateway,
    appDatabase: AppDatabase

) : ArtistGateway {

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private fun queryAllData(): Observable<List<Artist>> {
        return rxContentResolver.createQuery(
            MEDIA_STORE_URI, arrayOf("count(*) as size"), null,
            null, " size ASC LIMIT 1", true
        )
            .lift(SqlBrite.Query.mapToOne { 0 })
            .switchMap { songGateway.getAll() }
            .map { mapToArtists(it) }

    }

    private fun mapToArtists(songList: List<Song>): List<Artist> {
        return songList.asSequence()
//                .filter { it.artist != AppConstants.UNKNOWN } TODO
            .distinctBy { it.artistId }
            .map { song ->
                val albums = countAlbums(song.artistId, songList)
                val songs = countTracks(song.artistId, songList)
                mapSongToArtist(song, songs, albums)
            }
            .toList()
    }

    private fun mapSongToArtist(song: Song, songCount: Int, albumCount: Int): Artist {
        return song.toArtist(songCount, albumCount)
    }

    private fun countAlbums(artistId: Long, songList: List<Song>): Int {
        return songList.asSequence()
            .distinctBy { it.albumId }
//            .filter { it.album != AppConstants.UNKNOWN } TODO
            .count { it.artistId == artistId }
    }

    private fun countTracks(artistId: Long, songList: List<Song>): Int {
        return songList.count { it.artistId == artistId }
    }

    private val cachedData = queryAllData()
        .replay(1)
        .refCount()

    override fun getAll(): Observable<List<Artist>> {
        return cachedData
    }

    override fun getByParam(param: Long): Observable<Artist> {
        return cachedData.map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Observable<List<Song>> {
        return songGateway.getAll().map {
            it.asSequence().filter { it.artistId == artistId }.toList()
        }.distinctUntilChanged()
    }

    override fun getLastPlayed(): Observable<List<Artist>> {
        return Observables.combineLatest(
            getAll(),
            lastPlayedDao.getAll().toObservable()
        ) { all, lastPlayed ->

            if (all.size < 5) {
                listOf()
            } else {
                lastPlayed.asSequence()
                    .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                    .take(5)
                    .toList()
            }
        }
    }

    override fun addLastPlayed(id: Long): Completable {
        return lastPlayedDao.insertOne(id)
    }

}