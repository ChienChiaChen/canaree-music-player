package dev.olog.msc.musicservice.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.musicservice.notification.INotification
import dev.olog.msc.musicservice.notification.NotificationImpl21
import dev.olog.msc.musicservice.notification.NotificationImpl24
import dev.olog.msc.musicservice.notification.NotificationImpl26
import dev.olog.msc.shared.utils.isNougat
import dev.olog.msc.shared.utils.isOreo

@Module
abstract class NotificationModule {

    @Module
    companion object {
        @Provides
        @PerService
        @JvmStatic
        internal fun provideNotificationImpl(
            notificationImpl26: Lazy<NotificationImpl26>,
            notificationImpl24: Lazy<NotificationImpl24>,
            notificationImpl: Lazy<NotificationImpl21>

        ): INotification {
            return when {
                isOreo() -> notificationImpl26.get()
                isNougat() -> notificationImpl24.get()
                else -> notificationImpl.get()
            }
        }
    }

}
