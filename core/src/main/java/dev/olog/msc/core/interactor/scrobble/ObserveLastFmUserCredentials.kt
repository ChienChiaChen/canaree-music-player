package dev.olog.msc.core.interactor.scrobble

import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.ObservableFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
    schedulers: IoDispatcher,
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) : ObservableFlow<UserCredentials>(schedulers) {

    override suspend fun buildUseCaseObservable(): Flow<UserCredentials> {
        return gateway.observeLastFmCredentials()
            .map { decryptUser(it) }
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.decrypt(user.username),
            lastFmEncrypter.decrypt(user.password)
        )
    }

}