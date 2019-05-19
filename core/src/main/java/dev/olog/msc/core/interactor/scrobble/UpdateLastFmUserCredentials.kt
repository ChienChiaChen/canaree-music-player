package dev.olog.msc.core.interactor.scrobble

import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
    schedulers: ComputationDispatcher,
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) : CompletableFlowWithParam<UserCredentials>(schedulers) {

    override suspend fun buildUseCaseObservable(param: UserCredentials) {
        val user = encryptUser(param)
        gateway.setLastFmCredentials(user)
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.encrypt(user.username),
            lastFmEncrypter.encrypt(user.password)
        )
    }

}