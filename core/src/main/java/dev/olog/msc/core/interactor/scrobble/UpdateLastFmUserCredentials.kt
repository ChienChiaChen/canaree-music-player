package dev.olog.msc.core.interactor.scrobble

import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway,
        private val lastFmEncrypter: IEncrypter

) : CompletableUseCaseWithParam<UserCredentials>(schedulers) {

    override fun buildUseCaseObservable(param: UserCredentials): Completable {
        return Completable.create {
            val user = encryptUser(param)
            gateway.setLastFmCredentials(user)

            it.onComplete()
        }
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
                lastFmEncrypter.encrypt(user.username),
                lastFmEncrypter.encrypt(user.password)
        )
    }

}