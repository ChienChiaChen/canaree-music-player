package dev.olog.msc.core.gateway.prefs

interface TutorialPreferenceGateway {

    fun canShowSortByTutorial(): Boolean
    fun canShowFloatingWindowTutorial(): Boolean
    fun canShowLyricsTutorial(): Boolean
    fun canShowEditLyrics(): Boolean
    fun reset()

}