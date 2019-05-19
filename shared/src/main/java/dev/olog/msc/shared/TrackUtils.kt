package dev.olog.msc.shared

object TrackUtils {

    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun initialize(unknownArtist: String, unknownAlbum: String){
        UNKNOWN_ALBUM = unknownAlbum
        UNKNOWN_ARTIST = unknownArtist
    }

}