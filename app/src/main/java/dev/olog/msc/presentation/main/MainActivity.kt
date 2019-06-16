package dev.olog.msc.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.msc.Permissions
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.FloatingWindowsConstants
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.BottomNavigationPage
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.DrawsOnTop
import dev.olog.msc.presentation.base.CanHandleOnBackPressed
import dev.olog.msc.presentation.base.HasBilling
import dev.olog.msc.presentation.base.HasSlidingPanel
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.msc.presentation.base.music.service.MusicGlueActivity
import dev.olog.msc.presentation.dialog.rate.request.RateAppDialog
import dev.olog.msc.presentation.library.categories.track.CategoriesFragment
import dev.olog.msc.presentation.main.utils.toBottomNavigationPage
import dev.olog.msc.presentation.main.utils.toMenuId
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.k.extension.*
import dev.olog.scrollhelper.InitialHeight
import dev.olog.scrollhelper.Input
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling {

    @Inject lateinit var navigator: Navigator
    @Inject override lateinit var billing: IBilling
    @Inject lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Inject lateinit var rateAppDialog: RateAppDialog
    @Inject lateinit var viewModel: MainActivityPresenter

    private lateinit var scrollHelper: SuperCerealScrollHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollHelper = SuperCerealScrollHelper(this, Input.Full(
            slidingPanel = BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*> to InitialHeight(dimen(R.dimen.sliding_panel_peek)),
            bottomNavigation = bottomWrapper to InitialHeight(dimen(R.dimen.bottom_navigation_height)),
            toolbarHeight = InitialHeight(dimen(R.dimen.toolbar)),
            tabLayoutHeight = InitialHeight(dimen(R.dimen.tab))
        ))

        val isFirstAccess = isFirstAccess()

        when {
            isFirstAccess -> {
                // TODO hide sliding panel and bottom navigation?
                navigator.toFirstAccess()
            }
            savedInstanceState == null -> {
                BottomNavigator.initialize(this, viewModel.getLastBottomViewPage())
                handleOnActivityCreated()
            }
        }
        if (isFirstAccess) {
            return
        }

        viewModel.isRepositoryEmptyUseCase.execute()
            .asLiveData()
            .subscribe(this, this::handleEmptyRepository)

        setupMiniPlayerTheme()
        adjustSlidingPanel()

        intent?.let { handleIntent(it) }
    }

    private fun handleOnActivityCreated() {
        val bottomNavigationPage = viewModel.getLastBottomViewPage()
        var navigateTo = bottomNavigationPage.toMenuId()
        if (!viewModel.canShowPodcastCategory()) {
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
            if (navigateTo == R.id.navigation_podcasts) {
                navigateTo = R.id.navigation_songs
                viewModel.setLastBottomViewPage(navigateTo.toBottomNavigationPage())
            }
        }
        bottomNavigation.selectedItemId = navigateTo
        BottomNavigator.navigate(this, bottomNavigationPage)
    }

    private fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(this)
        val isFirstAccess = viewModel.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    private fun setupMiniPlayerTheme(){
        if (AppTheme.isMiniTheme()) {
//            slidingPanel.setParallaxOffset(0) TODO
//            playerLayout.layoutParams = SlidingUpPanelLayout.LayoutParams(
//                SlidingUpPanelLayout.LayoutParams.MATCH_PARENT, SlidingUpPanelLayout.LayoutParams.WRAP_CONTENT
//            )
        }
    }

    // slides down bottom navigation when sliding panel is expanded
    private fun adjustSlidingPanel() {
        bottomWrapper.doOnPreDraw {
            if (getSlidingPanel().isExpanded()) {
                bottomWrapper.translationY = bottomWrapper.height.toFloat()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.setOnNavigationItemSelectedListener { menu ->
            val navigationPage = menu.itemId.toBottomNavigationPage()
            viewModel.setLastBottomViewPage(navigationPage)
            BottomNavigator.navigate(this, navigationPage)
            true
        }
        bottomNavigation.setOnNavigationItemReselectedListener { /* do nothing */ }

        scrollHelper.onAttach()
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
        scrollHelper.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollHelper.dispose()
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
            }
            AppConstants.SHORTCUT_SEARCH -> {
                bottomNavigation.selectedItemId = R.id.navigation_search
                BottomNavigator.navigate(this, BottomNavigationPage.SEARCH)
            }
            AppConstants.ACTION_CONTENT_VIEW -> getSlidingPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            AppConstants.SHORTCUT_DETAIL -> {
                val string = intent.getStringExtra(AppConstants.SHORTCUT_DETAIL_MEDIA_ID)
                val mediaId = MediaId.fromString(string)
                navigator.toDetailFragment(mediaId)
            }
            Intent.ACTION_VIEW -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MusicConstants.ACTION_PLAY_FROM_URI
                serviceIntent.data = intent.data
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
        setIntent(null)
    }

    private fun handleEmptyRepository(isEmpty: Boolean) {
        // TODO handle in OnScrollBehavior.kt ?
        val height = if (isEmpty) {
            dimen(R.dimen.bottom_navigation_height)
        } else {
            dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
        }

        getSlidingPanel().peekHeight = height
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
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
        // TODO test if implementation is correct, i think it pop folder back even if not seeing the fragment
        val categoriesFragment = supportFragmentManager.findFragmentByTag(CategoriesFragment.TAG) ?: return false
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

    override fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*> {
        // TODO cache the value?
        return BottomSheetBehavior.from(slidingPanel) as MultiListenerBottomSheetBehavior<*>
    }
}