package dev.olog.msc.presentation.dialogs.duplicates.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog

@Module(subcomponents = arrayOf(RemoveDuplicatesDialogSubComponent::class))
abstract class RemoveDuplicatesDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(RemoveDuplicatesDialog::class)
    internal abstract fun injectorFactory(builder: RemoveDuplicatesDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
