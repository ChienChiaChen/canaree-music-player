package dev.olog.msc.presentation.home.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.core.dagger.scope.PerActivity
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.pro.ProModule

@Subcomponent(modules = arrayOf(
        MainActivityModule::class,
        MainActivityFragmentsModule::class,
        ProModule::class
//
//        // fragments
//        RecentlyAddedFragmentInjector::class,
//        RelatedArtistFragmentInjector::class,

//        PlayingQueueFragmentInjector::class,
//        PlaylistTracksChooserInjector::class,

        // dialogs
//        AddFavoriteDialogInjector::class,
//        PlayNextDialogInjector::class,
//        PlayLaterDialogInjector::class,
//        SetRingtoneDialogInjector::class,
//        RenameDialogInjector::class,
//        ClearPlaylistDialogInjector::class,
//        DeleteDialogInjector::class,
//        NewPlaylistDialogInjector::class,
//        RemoveDuplicatesDialogInjector::class TODO
))
@PerActivity
interface MainActivitySubComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>() {

        abstract fun module(module: MainActivityModule): Builder

        override fun seedInstance(instance: MainActivity) {
            module(MainActivityModule(instance))
        }
    }

}