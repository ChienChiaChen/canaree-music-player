package dev.olog.msc.core.interactor.scrobble

import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveLastFmUserCredentials @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway,
        private val lastFmEncrypter: IEncrypter

) : ObservableUseCase<UserCredentials>(schedulers) {

    override fun buildUseCaseObservable(): Observable<UserCredentials> {
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