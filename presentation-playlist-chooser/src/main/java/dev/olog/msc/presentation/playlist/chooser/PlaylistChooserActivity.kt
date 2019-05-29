package dev.olog.msc.presentation.playlist.chooser

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.presentation.base.activity.BaseActivity
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.shared.core.lazyFast
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import javax.inject.Inject

class PlaylistChooserActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
//    @Inject
//    lateinit var appShortcuts: AppShortcuts

    private val viewModel by lazyFast {
        viewModelProvider<PlaylistChooserActivityViewModel>(
            factory
        )
    }
//    private val adapter by lazyFast { PlaylistChooserActivityAdapter(this, appShortcuts) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

//        viewModel.observeData()
//                .subscribe(this, adapter::updateDataSet)

//        list.adapter = adapter TODO restore appshortucuts
        list.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun injectComponents() {

    }

}