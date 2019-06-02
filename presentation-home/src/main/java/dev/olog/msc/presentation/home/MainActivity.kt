package dev.olog.msc.presentation.home

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.presentation.base.FloatingWindowHelper
import dev.olog.msc.presentation.base.RateAppDialog
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.msc.presentation.base.extensions.*
import dev.olog.msc.presentation.base.interfaces.*
import dev.olog.msc.presentation.home.base.MusicGlueActivity
import dev.olog.msc.presentation.home.di.inject
import dev.olog.msc.presentation.home.utils.toBottomNavigationPage
import dev.olog.msc.presentation.home.utils.toMenuId
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.navigator.Services
import dev.olog.msc.pro.HasBilling
import dev.olog.msc.pro.IBilling
import dev.olog.msc.shared.*
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.ui.extensions.setGone
import dev.olog.msc.shared.ui.theme.miniPlayerTheme
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : MusicGlueActivity(),
    HasSlidingPanel,
    HasBottomNavigation,
    HasBilling {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val presenter by lazyFast { viewModelProvider<MainActivityViewModel>(factory) }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    override lateinit var billing: IBilling

    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior

    @Suppress("unused")
    @Inject
    lateinit var onScrollBehavior: OnScrollSlidingCerealBehavior

    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    override fun injectComponents() {
        inject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusBar.doOnPreDraw {
            // workaround for blurLayout that has problems blurring views when navigation bar is hidden
            blurView.setStatusBarHeight(statusBar.height)
            root.removeView(it)
        }

        setupMiniPlayerTheme()
        setupSlidingPanel()
//        presenter.observeIsRepositoryEmpty() TODo
//            .subscribe(this, this::handleEmptyRepository)

        handleEmptyRepository(false)

        val isFirstAccess = isFirstAccess()

        when {
            isFirstAccess -> navigator.toFirstAccess(this)
            savedInstanceState == null -> {
                BottomNavigator.initialize(this, presenter.getLastBottomViewPage())
                handleOnActivityCreated()
            }
        }
        if (isFirstAccess) {
            return
        }

        adjustSlidingPanel()

        intent?.let { handleIntent(it) }
    }

    private fun setupMiniPlayerTheme() {
        if (miniPlayerTheme().isOpaque()) {
            blurView.setGone()
            blurView.fps = 0
        } else {
            bottomWrapper.background = null
        }
    }

    private fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(this)
        val isFirstAccess = presenter.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    private fun setupSlidingPanel() {
        val params = slidingPanel.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = SuperCerealBottomSheetBehavior<View>()
    }

    private fun adjustSlidingPanel() {

        bottomWrapper.doOnPreDraw {
            if (getSlidingPanel().isExpanded()) {
                bottomWrapper.translationY = bottomWrapper.height.toFloat()
            }
        }
    }

    private fun handleOnActivityCreated() {
        val bottomNavigationPage = presenter.getLastBottomViewPage()
        var navigateTo = bottomNavigationPage.toMenuId()
        if (!presenter.canShowPodcastCategory()) {
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
            if (navigateTo == R.id.navigation_podcasts) {
                navigateTo = R.id.navigation_songs
                presenter.setLastBottomViewPage(navigateTo.toBottomNavigationPage())
            }
        }
        bottomNavigation.selectedItemId = navigateTo
        BottomNavigator.navigate(this, bottomNavigationPage)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            presenter.setLastBottomViewPage(navigationPage)
            BottomNavigator.navigate(this, navigationPage)
            true
        }
        bottomNavigation.setOnNavigationItemReselectedListener { /* do nothing */ }

//        slidingPanel.setFadeOnClickListener { TODo
//            if (playerTheme().isMini()){
//                slidingPanel.collapse()
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        bottomNavigation.setOnNavigationItemSelectedListener(null)
        bottomNavigation.setOnNavigationItemReselectedListener(null)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this, Services.floating())
            }
            ShortcutsConstants.SHORTCUT_SEARCH -> {
                bottomNavigation.selectedItemId = R.id.navigation_search
                BottomNavigator.navigate(this, BottomNavigationPage.SEARCH)
            }
            PendingIntents.ACTION_CONTENT_VIEW -> getSlidingPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, Services.music())
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            ShortcutsConstants.SHORTCUT_DETAIL -> {
                val string = intent.getStringExtra(ShortcutsConstants.SHORTCUT_DETAIL_MEDIA_ID)
                val mediaId = MediaId.fromString(string)
                navigator.toDetailFragment(this, mediaId)
            }
            Intent.ACTION_VIEW -> {
                val serviceIntent = Intent(this, Services.music())
                serviceIntent.action = MusicConstants.ACTION_PLAY_FROM_URI
                serviceIntent.data = intent.data
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
        setIntent(null)
    }

    private fun handleEmptyRepository(isEmpty: Boolean) {
        // TODO handle in OnScrollBehavior.kt
        val height = if (isEmpty) {
            dimen(R.dimen.bottom_navigation_height)
        } else {
            dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
        }

        getSlidingPanel().panelHeight = height
        val params = blurView.layoutParams as CoordinatorLayout.LayoutParams
        params.height = height
        blurView.layoutParams = params
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this, Services.floating())
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        try {
            val topFragment = getTopFragment()

            when {
                topFragment is DrawsOnTop || topFragment is DimBottomSheetDialogFragment -> {
                    super.onBackPressed()
                    return
                }
                getSlidingPanel().isExpanded() -> {
                    getSlidingPanel().collapse()
                    return
                }
            }
            if (tryPopFolderBack()) {
                return
            }

            super.onBackPressed()
        } catch (ex: IllegalStateException) {
            /*random fragment manager crashes */
            ex.printStackTrace()
        }

    }

    private fun tryPopFolderBack(): Boolean {
        val categoriesFragment = supportFragmentManager.findFragmentByTag(Fragments.CATEGORIES) ?: return false
        val fragments = categoriesFragment.childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is CanHandleOnBackPressed &&
                fragment.viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED && // ensure fragment is visible
                fragment.handleOnBackPressed()
            ) {
                return true
            }
        }
        return false
    }

    override fun getSlidingPanel(): SuperCerealBottomSheetBehavior<*> {
        return BottomSheetBehavior.from(slidingPanel) as SuperCerealBottomSheetBehavior<*>
    }

    override fun togglePodcast(show: Boolean) {
        if (show) {
            bottomNavigation.menu.removeItem(R.id.navigation_songs)
            bottomNavigation.menu.removeItem(R.id.navigation_queue)
            bottomNavigation.menu.removeItem(R.id.navigation_search)
            bottomNavigation.inflateMenu(R.menu.drawer)
        } else {
            if (bottomNavigation.selectedItemId == R.id.navigation_podcasts) {
                bottomNavigation.selectedItemId = R.id.navigation_songs
                BottomNavigator.navigate(this, BottomNavigationPage.SONGS)
            }
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
        }
    }
}

