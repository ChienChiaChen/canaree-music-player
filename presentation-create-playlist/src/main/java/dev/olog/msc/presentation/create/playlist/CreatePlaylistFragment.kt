package dev.olog.msc.presentation.create.playlist

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginBottom
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.presentation.base.extensions.act
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.extensions.fragmentTransaction
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.DrawsOnTop
import dev.olog.msc.presentation.base.utils.hideKeyboard
import dev.olog.msc.presentation.create.playlist.di.inject
import dev.olog.msc.presentation.navigator.Fragments
import dev.olog.msc.shared.core.flow.debounceFirst
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.bindinds.afterTextChange
import dev.olog.msc.shared.ui.extensions.*
import kotlinx.android.synthetic.main.fragment_create_playlist.*
import kotlinx.android.synthetic.main.fragment_create_playlist.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreatePlaylistFragment : BaseFragment(), DrawsOnTop, CoroutineScope by MainScope() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazyFast { viewModelProvider<CreatePlaylistViewModel>(viewModelFactory) }
    private val adapter by lazyFast { CreatePlaylistAdapter(viewModel) }

    private var toast: Toast? = null

    private val playlistType by lazyFast {
        PlaylistType.values()[arguments!!.getInt(Fragments.ARGUMENTS_PLAYLIST_TYPE)]
    }

    override fun injectComponent() {
        inject()
    }

    override fun onDetach() {
        val fragmentManager = activity?.supportFragmentManager
        act.fragmentTransaction {
            fragmentManager!!.findFragmentByTag(Fragments.CATEGORIES)?.let { show(it) }
            setReorderingAllowed(true)
        }
        super.onDetach()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        view.fab.setMargin(bottomPx = view.fab.marginBottom + ctx.dimen(R.dimen.sliding_panel_peek) + ctx.dimen(R.dimen.bottom_navigation_height))
        view.list.setPaddingBottom(ctx.dimen(R.dimen.sliding_panel_peek) + ctx.dip(8))

        view.toolbar.doOnPreDraw {
            view.list.setPaddingTop(it.height + ctx.dip(8))
        }

        viewModel.observeData(playlistType)
            .subscribe(this, adapter::submitList)

        viewModel.observeSelectedCount()
            .subscribe(viewLifecycleOwner) { size ->
                val text = when (size) {
                    0 -> getString(R.string.playlist_tracks_chooser_no_tracks)
                    else -> resources.getQuantityString(R.plurals.playlist_tracks_chooser_count, size, size)
                }
                header.text = text
                fab.toggleVisibility(size > 0, false)
            }

//        viewModel.getAllSongs(playlistType, filter(view))
//                .subscribe(viewLifecycleOwner) {
//                    view.sidebar.onDataChanged(it) TODO
//                }

//        adapter.setAfterDataChanged({
//            view.emptyStateText.toggleVisibility(it.isEmpty(), true)
//        }) TODO

        launch {
            view.editText.afterTextChange()
                .debounceFirst(200)
                .filter { it.isBlank() || it.trim().length >= 2 }
                .distinctUntilChanged()
                .collect { viewModel.updateFilter(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancel()
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener {
            editText.hideKeyboard()
            act.onBackPressed()
        }
        fab.setOnClickListener {
            showCreateDialog()
        }
        filterList.setOnClickListener {
            if (viewModel.toggleShowOnlyFiltered()) {
                filterList.toggleSelected()

                toast?.cancel()

                if (filterList.isSelected) {
                    toast = act.toast(R.string.playlist_tracks_chooser_show_only_selected)
                } else {
                    toast = act.toast(R.string.playlist_tracks_chooser_show_all)
                }
            } else {
                act.toast("No song selected")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
        fab.setOnClickListener(null)
        editText.setOnClickListener(null)
    }

    private fun showCreateDialog() {
        val builder = AlertDialog.Builder(act)
            .setTitle("New playlist")
            .setView(R.layout.layout_edit_text)
            .setPositiveButton(R.string.common_ok, null)
            .setNegativeButton(R.string.common_cancel, null)

//        val dialog = builder.show() TODO
//
//        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
//        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)!!
//        val clearButton = dialog.findViewById<View>(R.id.clear)!!
//        clearButton.setOnClickListener { editText.setText("") }
//
//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
//            val editTextString = editText.text.toString()
//            when {
//                editTextString.isBlank() -> showError(editTextLayout, "Playlist name not valid")
//                else -> {
//                    viewModel.savePlaylist(playlistType, editTextString,
//                        onSuccess = {
//                            dialog.dismiss()
//                            act.onBackPressed()
//                        },
//                        onFail = {
//                            ctx.toast("Something wen wrong")
//                        })
//
//                }
//            }
//        }
//
//        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }
//
//        dialog.show()
    }

    override fun onStop() {
        super.onStop()
//        errorDisposable.unsubscribe()
    }

//    private fun showError(editTextLayout: TextInputLayout, error: String) { TODO
//        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
//        editTextLayout.startAnimation(shake)
//        editTextLayout.error = error
//        editTextLayout.isErrorEnabled = true
//
//        errorDisposable.unsubscribe()
//        errorDisposable = Single.timer(2, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ editTextLayout.isErrorEnabled = false }, Throwable::printStackTrace)
//    }


    override fun provideLayoutId(): Int = R.layout.fragment_create_playlist
}