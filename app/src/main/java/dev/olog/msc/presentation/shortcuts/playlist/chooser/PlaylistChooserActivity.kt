package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.os.Bundle
import dev.olog.msc.R
import dev.olog.presentation.base.activity.BaseActivity
import dev.olog.msc.shared.extensions.isPortrait
import dev.olog.presentation.base.extensions.asLiveData
import dev.olog.presentation.base.extensions.subscribe
import kotlinx.android.synthetic.main.activity_playlist_chooser.*
import javax.inject.Inject

class PlaylistChooserActivity : BaseActivity() {

    @Inject lateinit var presenter: PlaylistChooserActivityViewPresenter
    @Inject lateinit var adapter: PlaylistChooserActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_chooser)

        presenter.execute(resources)
                .asLiveData()
                .subscribe(this, adapter::updateDataSet)

        list.adapter = adapter
        list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, if (isPortrait) 2 else 3)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

}