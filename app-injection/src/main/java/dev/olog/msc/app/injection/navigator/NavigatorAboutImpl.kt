package dev.olog.msc.app.injection.navigator

import androidx.fragment.app.FragmentActivity
import dev.olog.msc.presentation.navigator.NavigatorAbout
import javax.inject.Inject

private const val NEXT_REQUEST_THRESHOLD: Long = 400 // ms

class NavigatorAboutImpl @Inject constructor() : NavigatorAbout {

//    private var lastRequest: Long = -1
//
//    override fun toLicensesFragment(activity: FragmentActivity) {
//        if (allowed()) {
//            activity.fragmentTransaction {
//                setReorderingAllowed(true)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                add(R.id.fragment_container, LicensesFragment(), LicensesFragment.TAG)
//                addToBackStack(LicensesFragment.TAG)
//            }
//        }
//    }
//
//    override fun toSpecialThanksFragment(activity: FragmentActivity) {
//        if (allowed()) {
//            activity.fragmentTransaction {
//                setReorderingAllowed(true)
//                setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                add(R.id.fragment_container, SpecialThanksFragment(), SpecialThanksFragment.TAG)
//                addToBackStack(SpecialThanksFragment.TAG)
//            }
//        }
//    }
//
//    override fun toMarket(activity: FragmentActivity) {
//        if (allowed()) {
//            openPlayStore(activity)
//        }
//    }
//
//    override fun toPrivacyPolicy(activity: FragmentActivity) {
//        if (allowed()) {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("https://deveugeniuolog.wixsite.com/next/privacy-policy")
//            if (activity.packageManager.isIntentSafe(intent)) {
//                activity.startActivity(intent)
//            } else {
//                activity.toast("Browser not found")
//            }
//        }
//    }
//
//    override fun joinCommunity(activity: FragmentActivity) {
//        if (allowed()) {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("https://plus.google.com/u/1/communities/112263979767803607353")
//            if (activity.packageManager.isIntentSafe(intent)) {
//                activity.startActivity(intent)
//            } else {
//                activity.toast("Browser not found")
//            }
//        }
//    }
//
//    override fun joinBeta(activity: FragmentActivity) {
//        if (allowed()) {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("https://play.google.com/apps/testing/dev.olog.msc")
//            if (activity.packageManager.isIntentSafe(intent)) {
//                activity.startActivity(intent)
//            } else {
//                activity.toast("Browser not found")
//            }
//        }
//    }
//
//    private fun allowed(): Boolean {
//        val allowed = (System.currentTimeMillis() - lastRequest) > NEXT_REQUEST_THRESHOLD
//        lastRequest = System.currentTimeMillis()
//        return allowed
//    }

    override fun toLicensesFragment(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toSpecialThanksFragment(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toMarket(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toPrivacyPolicy(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun joinCommunity(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun joinBeta(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}