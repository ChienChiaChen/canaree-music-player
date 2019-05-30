package dev.olog.msc.presentation.navigator

object Services {

    private val classMap = mutableMapOf<String, Class<*>>()

    fun music(): Class<*> {
        return classMap.getOrPut("music") {
            Class.forName("dev.olog.msc.musicservice.MusicService")
        }
    }

    fun floating(): Class<*> {
        return classMap.getOrPut("floating") {
            Class.forName("dev.olog.msc.floatingwindowservice.FloatingWindowService")
        }
    }

}