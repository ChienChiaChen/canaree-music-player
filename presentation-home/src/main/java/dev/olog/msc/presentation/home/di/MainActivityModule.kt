package dev.olog.msc.presentation.home.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.home.MainActivity
import dev.olog.msc.pro.ProModule

@Module(includes = [ProModule::class])
class MainActivityModule(
        private val activity: MainActivity
) {

    @Provides
    fun provideInstance() = activity
//
//    @Provides
//    @ActivityContext
//    internal fun provideContext(): Context = activity
//
//    @Provides
//    @ActivityLifecycle
//    internal fun provideLifecycle(): Lifecycle = activity.lifecycle
//
//    @Provides
//    internal fun provideLifecycleOwner(): LifecycleOwner = activity
//

    @Provides
    internal fun provideSupportActivity(): AppCompatActivity = activity
//
//    @Provides
//    internal fun provideFragmentActivity(): androidx.fragment.app.FragmentActivity = activity

}