package dev.olog.msc.presentation.navigator

import androidx.fragment.app.FragmentActivity

interface NavigatorAbout {

    fun toLicensesFragment(activity: FragmentActivity)

    fun toSpecialThanksFragment(activity: FragmentActivity)

    fun toMarket(activity: FragmentActivity)

    fun toPrivacyPolicy(activity: FragmentActivity)

    fun joinCommunity(activity: FragmentActivity)

    fun joinBeta(activity: FragmentActivity)

}