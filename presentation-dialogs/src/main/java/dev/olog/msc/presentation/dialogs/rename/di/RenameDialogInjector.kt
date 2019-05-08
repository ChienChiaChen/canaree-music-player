package dev.olog.msc.presentation.dialogs.rename.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialogs.rename.RenameDialog


@Module(subcomponents = arrayOf(RenameDialogSubComponent::class))
abstract class RenameDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(RenameDialog::class)
    internal abstract fun injectorFactory(builder: RenameDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
