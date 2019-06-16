package dev.olog.msc.presentation.main.utils

import dev.olog.msc.R
import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.presentation.library.categories.podcast.CategoriesPodcastFragment
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment
import dev.olog.msc.presentation.playing.queue.PlayingQueueFragment
import dev.olog.msc.presentation.search.SearchFragment

internal fun BottomNavigationPage.toMenuId(): Int = when (this){
    BottomNavigationPage.SONGS -> R.id.navigation_songs
    BottomNavigationPage.PODCASTS -> R.id.navigation_podcasts
    BottomNavigationPage.SEARCH -> R.id.navigation_search
    BottomNavigationPage.QUEUE -> R.id.navigation_queue
}

internal fun Int.toBottomNavigationPage(): BottomNavigationPage = when (this){
    R.id.navigation_songs -> BottomNavigationPage.SONGS
    R.id.navigation_podcasts -> BottomNavigationPage.PODCASTS
    R.id.navigation_search -> BottomNavigationPage.SEARCH
    R.id.navigation_queue -> BottomNavigationPage.QUEUE
    else -> throw IllegalArgumentException("invalid menu id")
}

fun BottomNavigationPage.toFragmentTag(): String = when(this){
    BottomNavigationPage.SONGS -> CategoriesFragment.TAG
    BottomNavigationPage.PODCASTS -> CategoriesPodcastFragment.TAG
    BottomNavigationPage.SEARCH -> SearchFragment.TAG
    BottomNavigationPage.QUEUE -> PlayingQueueFragment.TAG
}