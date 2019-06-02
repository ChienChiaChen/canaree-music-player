package dev.olog.msc.presentation.home.utils

import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.presentation.home.R
import dev.olog.msc.presentation.navigator.Fragments

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
    BottomNavigationPage.SONGS -> Fragments.CATEGORIES
    BottomNavigationPage.PODCASTS -> Fragments.CATEGORIES_PODCAST
    BottomNavigationPage.SEARCH -> Fragments.SEARCH
    BottomNavigationPage.QUEUE -> Fragments.PLAYING_QUEUE
}