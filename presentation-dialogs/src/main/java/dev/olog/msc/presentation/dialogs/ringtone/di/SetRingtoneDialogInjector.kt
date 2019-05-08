package dev.olog.msc.presentation.dialogs.ringtone.di

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.dialogs.ringtone.SetRingtoneDialog

@Module(subcomponents = arrayOf(SetRingtoneDialogSubComponent::class))
abstract class SetRingtoneDialogInjector {

    @Binds
    @IntoMap
    @ClassKey(SetRingtoneDialog::class)
    internal abstract fun injectorFactory(builder: SetRingtoneDialogSubComponent.Builder)
            : AndroidInjector.Factory<*>

}
