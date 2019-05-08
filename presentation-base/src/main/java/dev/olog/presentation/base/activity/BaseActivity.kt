package dev.olog.presentation.base.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.shared.ui.theme.AppTheme
import dev.olog.presentation.base.R
import dev.olog.presentation.base.extensions.setLightStatusBar
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), ThemedActivity, HasSupportFragmentInjector {

    @Inject
    lateinit var prefsKeys: PrefsKeys

    @Inject
    internal lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getActivityTheme())
        AndroidInjection.inject(this)
        themeAccentColor(this, theme, prefsKeys)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    @StyleRes
    private fun getActivityTheme(): Int = when {
        AppTheme.isWhiteMode() -> R.style.AppThemeWhite
        AppTheme.isGrayMode() -> R.style.AppThemeGray
        AppTheme.isDarkMode() -> R.style.AppThemeDark
        AppTheme.isBlackMode() -> R.style.AppThemeBlack
        else -> throw IllegalStateException("invalid theme")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Fragment> findFragmentByTag(tag: String): T? {
        return supportFragmentManager.findFragmentByTag(tag) as T?
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && AppTheme.isImmersiveMode()) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return supportFragmentInjector
    }
}
