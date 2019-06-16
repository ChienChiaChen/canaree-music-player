package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseActivity
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
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

}