package dev.olog.msc.presentation.edititem.track.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edititem.track.EditTrackFragment

@Module(subcomponents = arrayOf(EditTrackFragmentSubComponent::class))
abstract class EditTrackFragmentInjector {

    @Binds
    @IntoMap
    @ClassKey(EditTrackFragment::class)
    internal abstract fun injectorFactory(builder: EditTrackFragmentSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
