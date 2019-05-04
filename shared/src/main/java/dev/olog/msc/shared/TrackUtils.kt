package dev.olog.msc.shared

object TrackUtils {

    const val UNKNOWN = "<unknown>"
    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun initialize(unknownArtist: String, unknownAlbum: String){
        UNKNOWN_ALBUM = unknownAlbum
        UNKNOWN_ARTIST = unknownArtist
    }

    fun adjustArtist(data: String): String{
        if (data == UNKNOWN){
            return UNKNOWN_ARTIST
        }
        return data
    }

    fun adjustAlbum(data: String): String{
        if (data == UNKNOWN){
            return UNKNOWN_ALBUM
        }
        return data
    }

}