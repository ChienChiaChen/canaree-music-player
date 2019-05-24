package dev.olog.msc

import dev.olog.msc.core.Classes
import dev.olog.msc.floatingwindowservice.FloatingWindowService
import dev.olog.msc.musicservice.MusicService
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity
import dev.olog.msc.presentation.shortcuts.playlist.chooser.PlaylistChooserActivity
import org.junit.Assert
import org.junit.Test

class ClassExistenceTest {

    @Test
    fun mainTest() {
        Assert.assertEquals(Classes.mainActivity, MainActivity::class.java)
        Assert.assertEquals(Classes.musicService, MusicService::class.java)
        Assert.assertEquals(Classes.floatingWindowService, FloatingWindowService::class.java)
        Assert.assertEquals(Classes.shortcutActivity, ShortcutsActivity::class.java)
        Assert.assertEquals(Classes.playlistChooser, PlaylistChooserActivity::class.java)
    }

}