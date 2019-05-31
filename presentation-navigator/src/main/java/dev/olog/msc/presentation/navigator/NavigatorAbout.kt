package dev.olog.msc.presentation.navigator

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

class NavigatorAbout @Inject constructor(){
//
//    fun toLicensesFragment(activity: FragmentActivity) {
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
//    fun toSpecialThanksFragment(activity: FragmentActivity) {
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
//    fun toMarket(activity: FragmentActivity) {
//        if (allowed()) {
//            openPlayStore(activity)
//        }
//    }
//
//    fun toPrivacyPolicy(activity: FragmentActivity) {
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
//    fun joinCommunity(activity: FragmentActivity) {
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
//    fun joinBeta(activity: FragmentActivity) {
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

    fun toLicensesFragment(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toSpecialThanksFragment(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toMarket(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toPrivacyPolicy(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun joinCommunity(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun joinBeta(activity: FragmentActivity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}