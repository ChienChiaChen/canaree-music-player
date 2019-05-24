package dev.olog.msc.presentation.home

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.*
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.FloatingWindowHelper
import dev.olog.msc.presentation.base.FragmentTags
import dev.olog.msc.presentation.base.RateAppDialog
import dev.olog.msc.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.msc.presentation.base.extensions.collapse
import dev.olog.msc.presentation.base.extensions.expand
import dev.olog.msc.presentation.base.extensions.getTopFragment
import dev.olog.msc.presentation.base.extensions.isExpanded
import dev.olog.msc.presentation.base.interfaces.CanHandleOnBackPressed
import dev.olog.msc.presentation.base.interfaces.DrawsOnTop
import dev.olog.msc.presentation.base.interfaces.HasBilling
import dev.olog.msc.presentation.base.interfaces.HasSlidingPanel
import dev.olog.msc.presentation.base.theme.player.theme.isMini
import dev.olog.msc.presentation.home.base.MusicGlueActivity
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.pro.IBilling
import dev.olog.msc.shared.*
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.ui.extensions.subscribe
import dev.olog.msc.shared.utils.clamp
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling {

    @Inject
    lateinit var presenter: MainActivityViewModel
    @Inject
    lateinit var navigator: Navigator
    // handles lifecycle itself
    @Inject
    override lateinit var billing: IBilling

    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slidingPanel.panelHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)

        presenter.observeIsRepositoryEmpty()
            .subscribe(this, this::handleEmptyRepository)

        val isFirstAccess = isFirstAccess()

        when {
            isFirstAccess -> navigator.toFirstAccess(this)
            savedInstanceState == null -> handleOnActivityCreated()
            else -> handleOnActivityResumed()
        }
        if (isFirstAccess) {
            return
        }

        adjustSlidingPanel()

        intent?.let { handleIntent(it) }
    }

    private fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(this)
        val isFirstAccess = presenter.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    private fun adjustSlidingPanel() {
        if (isMini()) {
            slidingPanel.setParallaxOffset(0)
            playerLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        bottomWrapper.doOnPreDraw {
            if (slidingPanel.isExpanded()) {
                bottomWrapper.translationY = bottomWrapper.height.toFloat()
            }
        }
    }

    private fun handleOnActivityCreated() {
        var navigateTo = presenter.getLastBottomViewPage()
        if (!presenter.canShowPodcastCategory()) {
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
            if (navigateTo == R.id.navigation_podcasts) {
                navigateTo = R.id.navigation_songs
                presenter.setLastBottomViewPage(navigateTo)
            }
        }
        bottomNavigation.selectedItemId = navigateTo
        bottomNavigate(navigateTo, false)
    }

    private fun handleOnActivityResumed() {
        if (!presenter.canShowPodcastCategory()) {
            val currentId = presenter.getLastBottomViewPage()
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
            if (currentId == R.id.navigation_podcasts) {
                bottomNavigation.selectedItemId = R.id.navigation_songs
                presenter.setLastBottomViewPage(R.id.navigation_songs)
                bottomNavigate(bottomNavigation.selectedItemId, true)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.setOnNavigationItemSelectedListener {
            presenter.setLastBottomViewPage(it.itemId)
            bottomNavigate(it.itemId, false)
            true
        }
        bottomNavigation.setOnNavigationItemReselectedListener { bottomNavigate(it.itemId, true) }
        slidingPanel.addPanelSlideListener(onPanelSlide)
        handleFakeView(slidingPanel.panelState)
    }

    private fun handleFakeView(state: PanelState) {
        when (state) {
            PanelState.EXPANDED,
            PanelState.ANCHORED -> {
                fakeView.isClickable = true
                fakeView.isFocusable = true
                fakeView.setOnClickListener { slidingPanel.collapse() }
            }
            else -> {
                fakeView.setOnClickListener(null)
                fakeView.isClickable = false
                fakeView.isFocusable = false
            }
        }
    }

    private fun bottomNavigate(itemId: Int, forceRecreate: Boolean) {
        when (itemId) {
            R.id.navigation_songs -> navigator.toLibraryCategories(this, forceRecreate)
            R.id.navigation_search -> navigator.toSearchFragment(this)
            R.id.navigation_podcasts -> navigator.toPodcastCategories(this, forceRecreate)
            R.id.navigation_queue -> navigator.toPlayingQueueFragment(this)
            else -> bottomNavigate(R.id.navigation_songs, forceRecreate)
        }
    }

    override fun onPause() {
        super.onPause()
        bottomNavigation.setOnNavigationItemSelectedListener(null)
        bottomNavigation.setOnNavigationItemReselectedListener(null)
        slidingPanel.removePanelSlideListener(onPanelSlide)
    }

    private val onPanelSlide = object : PanelSlideListener {

        override fun onPanelSlide(panel: View, slideOffset: Float) {
            bottomWrapper.translationY = bottomWrapper.height * clamp(slideOffset, 0f, 1f)
        }

        override fun onPanelStateChanged(
            panel: View,
            previousState: PanelState,
            newState: PanelState
        ) {
            handleFakeView(newState)
        }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this, classes.musicService())
            }
            ShortcutsConstants.SHORTCUT_SEARCH -> {
                bottomNavigation.selectedItemId = R.id.navigation_search
                navigator.toSearchFragment(this)
            }
            PendingIntents.ACTION_CONTENT_VIEW -> slidingPanel.expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, classes.musicService())
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            ShortcutsConstants.SHORTCUT_DETAIL -> {
                val string = intent.getStringExtra(ShortcutsConstants.SHORTCUT_DETAIL_MEDIA_ID)
                val mediaId = MediaId.fromString(string)
                navigator.toDetailFragment(this, mediaId)
            }
            Intent.ACTION_VIEW -> {
                val serviceIntent = Intent(this, classes.musicService())
                serviceIntent.action = MusicConstants.ACTION_PLAY_FROM_URI
                serviceIntent.data = intent.data
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
        setIntent(null)
    }

    private fun handleEmptyRepository(isEmpty: Boolean) {
        if (isEmpty) {
            slidingPanel.panelHeight = dimen(R.dimen.bottom_navigation_height)
        } else {
            slidingPanel.panelHeight = dimen(R.dimen.sliding_panel_peek) + dimen(R.dimen.bottom_navigation_height)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this, classes.floatingWindowService())
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
                slidingPanel.isExpanded() -> {
                    slidingPanel.collapse()
                    return
                }
            }
            if (tryPopFolderBack()) {
                return
            }

            super.onBackPressed()
        } catch (ex: IllegalStateException) { /*random fragment manager crashes */
        }

    }

    private fun tryPopFolderBack(): Boolean {
        val categoriesFragment = supportFragmentManager.findFragmentByTag(FragmentTags.CATEGORIES)
        val fragments = categoriesFragment!!.childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is CanHandleOnBackPressed && fragment.handleOnBackPressed()) {
                return true
            }
        }
        return true
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout = slidingPanel
}