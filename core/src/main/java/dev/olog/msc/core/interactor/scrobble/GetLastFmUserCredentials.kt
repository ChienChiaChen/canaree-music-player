package dev.olog.msc.core.interactor.scrobble

import dev.olog.msc.core.IEncrypter
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import javax.inject.Inject

class GetLastFmUserCredentials @Inject constructor(
        private val gateway: AppPreferencesGateway,
        private val lastFmEncrypter: IEncrypter

) {

    fun execute(): UserCredentials {
        return decryptUser(gateway.getLastFmCredentials())
    }

    private fun decryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
                lastFmEncrypter.decrypt(user.username),
                lastFmEncrypter.decrypt(user.password)
        )
    }

}