package dev.olog.msc.pro

import kotlinx.coroutines.flow.Flow

interface IBilling {

    fun isTrial(): Boolean
    fun isPremium(): Boolean
    fun isOnlyPremium(): Boolean
    fun observeIsPremium(): Flow<Boolean>
    fun observeTrialPremiumState(): Flow<BillingState>
    fun purchasePremium()

}