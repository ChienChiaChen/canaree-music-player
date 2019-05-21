package dev.olog.msc.presentation.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.core.content.edit
import androidx.core.os.bundleOf
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.presentation.base.activity.ThemedActivity
import dev.olog.msc.presentation.base.extensions.setLightStatusBar
import dev.olog.msc.presentation.base.interfaces.HasBilling
import dev.olog.msc.pro.IBilling
import dev.olog.msc.shared.ui.theme.HasImmersive
import kotlinx.android.synthetic.main.activity_preferences.*
import javax.inject.Inject

class PreferencesActivity : DaggerAppCompatActivity(),
        ColorCallback, ThemedActivity, HasBilling {

    companion object {
        const val EXTRA_NEED_TO_RECREATE = "EXTRA_NEED_TO_RECREATE"
    }

    @Inject override lateinit var billing: IBilling
    @Inject lateinit var prefsKeys: PrefsKeys

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        themeAccentColor(this, theme, prefsKeys)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
        setContentView(R.layout.activity_preferences)

        if (intent?.extras?.getBoolean(EXTRA_NEED_TO_RECREATE, false) == true) {
            setResult(Activity.RESULT_OK)
        }
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val key = getString(R.string.prefs_color_accent_key)
        prefs.edit {
            putInt(key, color)
        }
        recreateActivity()
    }

    fun recreateActivity() {
        val fragment = supportFragmentManager.findFragmentByTag("prefs") as PreferencesFragment?
        fragment?.let {
            it.requestMainActivityToRecreate()
            finish()
            startActivity(Intent(this, this::class.java),
                    bundleOf(EXTRA_NEED_TO_RECREATE to true)
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && (applicationContext as HasImmersive).isEnabled()){
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

}