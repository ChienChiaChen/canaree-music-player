package dev.olog.msc.presentation.navigator

import android.content.Context
import android.content.Intent

object Intents {

    fun splashActivity(context: Context): Intent {
        return Intent(context, Activities.splash())
    }

    fun preferenceActivity(context: Context): Intent {
        return Intent(context, Activities.settings())
    }

}