package dev.olog.msc.presentation.base.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import dev.olog.msc.core.PrefsKeys
import dev.olog.msc.presentation.base.extensions.setLightStatusBar
import dev.olog.msc.shared.ui.theme.HasImmersive
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(),
    ThemedActivity,
    HasSupportFragmentInjector {

    @Inject
    lateinit var prefsKeys: PrefsKeys

    @Inject
    internal lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        themeAccentColor(this, theme, prefsKeys)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && (applicationContext as HasImmersive).isEnabled()) {
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
