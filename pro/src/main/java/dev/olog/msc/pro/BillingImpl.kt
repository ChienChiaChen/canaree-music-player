package dev.olog.msc.pro

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.*
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.EqualizerPreferencesGateway
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.shared.core.coroutines.CustomScope
import dev.olog.msc.shared.core.coroutines.combineLatest
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

internal class BillingImpl @Inject constructor(
    private val activity: AppCompatActivity,
    private val appPrefsUseCase: AppPreferencesGateway,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val equalizerPrefsUseCase: EqualizerPreferencesGateway

) : IBilling, PurchasesUpdatedListener, DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    companion object {
        private const val PRO_VERSION_ID = "pro_version"
        @JvmStatic
        private val DEFAULT_PREMIUM = BuildConfig.DEBUG
        @JvmStatic
        private val DEFAULT_TRIAL = false
        @JvmStatic
        private val TRIAL_TIME = TimeUnit.HOURS.toMillis(1L)
    }

    private var isConnected = false

    private val premiumPublisher = BroadcastChannel<Boolean>(Channel.CONFLATED)
    @UseExperimental(ExperimentalCoroutinesApi::class)
    private val trialPublisher = BroadcastChannel<Boolean>(Channel.CONFLATED)

    private var isTrialState by Delegates.observable(DEFAULT_TRIAL) { _, _, new ->
        launch { trialPublisher.send(new) }
        if (!isPremium()) {
            setDefault()
        }
    }

    private var isPremiumState by Delegates.observable(DEFAULT_PREMIUM) { _, _, new ->
        launch { premiumPublisher.send(new) }
        if (!isPremium()) {
            setDefault()
        }
    }

    private val billingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .build()

    init {
        launch(Dispatchers.Main) {
            premiumPublisher.send(DEFAULT_PREMIUM)
            trialPublisher.send(DEFAULT_TRIAL)
        }
        activity.lifecycle.addObserver(this)
        startConnection { checkPurchases() }

        isTrialState = true
        launch(Dispatchers.IO) {
            while (isStillTrial()) {
                isTrialState = true
                delay(TimeUnit.MINUTES.toMillis(5))
            }
            isTrialState = false
        }
    }

    private fun isStillTrial(): Boolean {
        val packageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
        val firstInstallTime = packageInfo.firstInstallTime
        return System.currentTimeMillis() - firstInstallTime < TRIAL_TIME
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
        cancel()
    }

    private fun startConnection(func: (() -> Unit)?) {
        if (isConnected) {
            func?.invoke()
            return
        }

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(responseCode: Int) {
//                println("onBillingSetupFinished with response code:$responseCode")

                when (responseCode) {
                    BillingClient.BillingResponse.OK -> isConnected = true
                    BillingClient.BillingResponse.BILLING_UNAVAILABLE -> activity.toast("Play store not found")
                    // TODO
                }
                func?.invoke()
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    private fun checkPurchases() {
        val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        if (purchases.responseCode == BillingClient.BillingResponse.OK) {
            isPremiumState = isProBought(purchases.purchasesList)
        }
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                isPremiumState = isProBought(purchases)
            }
            // TODO
//            else -> Log.w("Billing", "billing response code=$responseCode")
        }
    }

    private fun isProBought(purchases: MutableList<Purchase>?): Boolean {
        return purchases?.firstOrNull { it.sku == PRO_VERSION_ID } != null || BuildConfig.DEBUG
//        return true
    }

    override fun isTrial(): Boolean = isTrialState

    override fun isPremium(): Boolean = isTrialState || isPremiumState

    override fun isOnlyPremium(): Boolean = isPremiumState

    override fun observeIsPremium(): Flow<Boolean> {
        return combineLatest(
            premiumPublisher.openSubscription(),
            trialPublisher.openSubscription()
        ) { isPremium, isTrial ->
            isPremium || isTrial
        }.distinctUntilChanged()
    }

    override fun observeTrialPremiumState(): Flow<BillingState> {
        return combineLatest(
            premiumPublisher.openSubscription(),
            trialPublisher.openSubscription()
        ) { isPremium, isTrial ->
            BillingState(isTrial, isPremium)
        }.distinctUntilChanged()
    }

    override fun purchasePremium() {
        startConnection {
            val params = BillingFlowParams.newBuilder()
                .setSku(PRO_VERSION_ID)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.launchBillingFlow(activity, params)
        }
    }

    private fun setDefault() = launch {
        assertBackgroundThread()
        appPrefsUseCase.setDefault()
        musicPreferencesUseCase.setDefault()
        equalizerPrefsUseCase.setDefault()
    }
}