package dev.olog.msc.floatingwindowservice

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.core.dagger.qualifier.ServiceContext
import dev.olog.msc.core.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.floatingwindowservice.api.HoverMenu
import dev.olog.msc.floatingwindowservice.api.view.TabView
import dev.olog.msc.floatingwindowservice.music.service.MusicGlueService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import java.net.URLEncoder
import javax.inject.Inject
import kotlin.properties.Delegates

internal class CustomHoverMenu @Inject constructor(
    @ServiceContext private val context: Context,
    @ServiceLifecycle lifecycle: Lifecycle,
    glueService: MusicGlueService,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val offlineLyricsContentPresenter: OfflineLyricsContentPresenter

) : HoverMenu(), DefaultLifecycleObserver {

    private val youtubeColors = intArrayOf(0xffe02773.toInt(), 0xfffe4e33.toInt())
    private val lyricsColors = intArrayOf(0xFFf79f32.toInt(), 0xFFfcca1c.toInt())
    private val offlineLyricsColors = intArrayOf(0xFFa3ffaa.toInt(), 0xFF1bffbc.toInt())

    private val lyricsContent = LyricsContent(context, glueService)
    private val videoContent = VideoContent(context)
    private val offlineLyricsContent = OfflineLyricsContent(context, glueService, offlineLyricsContentPresenter)

    private var item by Delegates.observable("", { _, _, new ->
        sections.forEach {
            if (it.content is WebViewContent) {
                (it.content as WebViewContent).item = URLEncoder.encode(new, "UTF-8")
            }
        }
    })

    private var job: Job? = null

    init {
        lifecycle.addObserver(this)
    }

    fun startObserving() {
        job?.cancel()
        job = GlobalScope.launch {
            musicPreferencesUseCase.observeLastMetadata()
                .filter { it.isNotEmpty() }
                .collect {
                    withContext(Dispatchers.Main) {
                        item = it.description
                    }
                }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job?.cancel()
        offlineLyricsContentPresenter.onDestroy()
    }

    private val lyricsSection = Section(
        SectionId("lyrics"),
        createTabView(lyricsColors, R.drawable.vd_lyrics_wrapper),
        lyricsContent
    )

    private val videoSection = Section(
        SectionId("video"),
        createTabView(youtubeColors, R.drawable.vd_video_wrapper),
        videoContent
    )

    private val offlineLyricsSection = Section(
        SectionId("offline_lyrics"),
        createTabView(offlineLyricsColors, R.drawable.vd_offline_lyrics_wrapper),
        offlineLyricsContent
    )

    private val sections: List<Section> = listOf(
        lyricsSection, videoSection, offlineLyricsSection
    )

    private fun createTabView(backgroundColors: IntArray, @DrawableRes icon: Int): TabView {
        return TabView(context, backgroundColors, icon)
    }

    override fun getId(): String = "menu id"

    override fun getSectionCount(): Int = sections.size

    override fun getSection(index: Int): Section? = sections[index]

    override fun getSection(sectionId: SectionId): Section? {
        return sections.find { it.id == sectionId }
    }

    override fun getSections(): List<Section> = sections.toList()

}