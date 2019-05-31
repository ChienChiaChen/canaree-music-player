package dev.olog.msc.presentation.navigator

object Activities {

    private val classMap = mutableMapOf<String, Class<*>>()

    fun main(): Class<*> {
        return classMap.getOrPut("main") {
            Class.forName("dev.olog.msc.presentation.home.MainActivity")
        }
    }

    fun splash(): Class<*> {
        return classMap.getOrPut("splash") {
            Class.forName("dev.olog.msc.presentation.splash.SplashActivity")
        }
    }

    fun shortcuts(): Class<*> {
        return classMap.getOrPut("shortcuts") {
            Class.forName("dev.olog.msc.appshortcuts.ShortcutsActivity")
        }
    }

    fun newPlaylist(): Class<*> {
        return classMap.getOrPut("newPlaylist") {
            Class.forName("dev.olog.msc.presentation.playlist.chooser.PlaylistChooserActivity")
        }
    }

}