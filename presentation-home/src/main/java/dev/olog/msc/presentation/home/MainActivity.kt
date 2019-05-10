package dev.olog.msc.presentation.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.categories.track.CategoriesFragment
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.pro.IBilling
import dev.olog.msc.shared.*
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.utils.clamp
import dev.olog.presentation.base.ActivityCodes
import dev.olog.presentation.base.FloatingWindowHelper
import dev.olog.presentation.base.RateAppDialog
import dev.olog.presentation.base.activity.MusicGlueActivity
import dev.olog.presentation.base.bottom.sheet.DimBottomSheetDialogFragment
import dev.olog.presentation.base.extensions.*
import dev.olog.presentation.base.interfaces.*
import dev.olog.presentation.base.theme.player.theme.isMini
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : MusicGlueActivity(), HasSlidingPanel, HasBilling {

    companion object {
        private const val SPLASH_REQUEST_CODE = 0
    }

    @Inject
    lateinit var presenter: MainActivityPresenter
    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var classes: Classes
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

        val canReadStorage = Permissions.canReadStorage(this)
        val isFirstAccess = presenter.isFirstAccess()
        val toFirstAccess = !canReadStorage || isFirstAccess
        if (toFirstAccess) {
            navigator.toFirstAccess(this, SPLASH_REQUEST_CODE)
            return
        } else if (savedInstanceState == null) {
            handleOnActivityCreated()
        } else {
            handleOnActivityResumed()
        }

        if (isMini()) {
            slidingPanel.setParallaxOffset(0)
            playerLayout.layoutParams = SlidingUpPanelLayout.LayoutParams(
                SlidingUpPanelLayout.LayoutParams.MATCH_PARENT, SlidingUpPanelLayout.LayoutParams.WRAP_CONTENT
            )
        }

        bottomWrapper.doOnPreDraw {
            if (slidingPanel.isExpanded()) {
                bottomWrapper.translationY = bottomWrapper.height.toFloat()
            }
        }

        intent?.let { handleIntent(it) }
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
        if (!presenter.canShowPodcastCategory()){
            val currentId = presenter.getLastBottomViewPage()
            bottomNavigation.menu.removeItem(R.id.navigation_podcasts)
            if (currentId == R.id.navigation_podcasts){
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

    private fun handleFakeView(state: SlidingUpPanelLayout.PanelState) {
        when (state) {
            SlidingUpPanelLayout.PanelState.EXPANDED,
            SlidingUpPanelLayout.PanelState.ANCHORED -> {
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

    private val onPanelSlide = object : SlidingUpPanelLayout.PanelSlideListener {

        override fun onPanelSlide(panel: View, slideOffset: Float) {
            bottomWrapper.translationY = bottomWrapper.height * clamp(slideOffset, 0f, 1f)
        }

        override fun onPanelStateChanged(
            panel: View,
            previousState: SlidingUpPanelLayout.PanelState,
            newState: SlidingUpPanelLayout.PanelState
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SPLASH_REQUEST_CODE -> {
                    bottomNavigate(bottomNavigation.selectedItemId, false)
                    slidingPanel.collapse()
                    return
                }
                ActivityCodes.REQUEST_CODE -> {
                    bottomNavigate(bottomNavigation.selectedItemId, true)
                    recreate()
                    return
                }
            }
        }

        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this, classes.floatingWindowService())
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        try {
            if (tryPopFolderBack()) {
                return
            }

            val topFragment = getTopFragment()

            when {
                topFragment is HasSafeTransition && topFragment.isAnimating() -> {
//                  prevents circular reveal crash
                }
                topFragment is DrawsOnTop -> super.onBackPressed()
                topFragment is DimBottomSheetDialogFragment -> supportFragmentManager.popBackStack()
                slidingPanel.isExpanded() -> slidingPanel.collapse()
                else -> super.onBackPressed()
            }
        } catch (ex: IllegalStateException) { /*random fragment manager crashes */
        }

    }

    private fun tryPopFolderBack(): Boolean {
        val categories = findFragmentByTag<CategoriesFragment>(CategoriesFragment.TAG)
        categories?.view?.findViewById<androidx.viewpager.widget.ViewPager>(R.id.viewPager)?.let { pager ->
            val currentItem = pager.adapter?.instantiateItem(pager, pager.currentItem) as Fragment

            return if (currentItem is CanHandleOnBackPressed) {
                currentItem.handle()
            } else false

        } ?: return false
    }

    override fun getSlidingPanel(): SlidingUpPanelLayout? = slidingPanel
}