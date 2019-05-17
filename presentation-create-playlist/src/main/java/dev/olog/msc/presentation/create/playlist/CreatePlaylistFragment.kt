package dev.olog.msc.presentation.create.playlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import dev.olog.msc.core.entity.PlaylistType
import dev.olog.msc.presentation.base.FragmentTags
import dev.olog.msc.presentation.base.extensions.*
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.DrawsOnTop
import dev.olog.msc.presentation.base.utils.ImeUtils
import dev.olog.msc.shared.extensions.debounceFirst
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.msc.shared.ui.ThemedDialog
import dev.olog.msc.shared.ui.extensions.toggleSelected
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_playlist_track_chooser.*
import kotlinx.android.synthetic.main.fragment_playlist_track_chooser.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreatePlaylistFragment : BaseFragment(), DrawsOnTop {

    companion object {
        const val TAG = "PlaylistTracksChooserFragment"
        const val ARGUMENT_PLAYLIST_TYPE = "$TAG.argument.playlist_type"

        @JvmStatic
        fun newInstance(type: PlaylistType): CreatePlaylistFragment {
            return CreatePlaylistFragment().withArguments(
                ARGUMENT_PLAYLIST_TYPE to type.ordinal
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<CreatePlaylistViewModel>(viewModelFactory) }
    private val adapter by lazyFast { CreatePlaylistAdapter(viewModel) }

    private var toast: Toast? = null

    private var errorDisposable: Disposable? = null

    private val playlistType by lazyFast {
        PlaylistType.values()[arguments!!.getInt(ARGUMENT_PLAYLIST_TYPE)]
    }

    override fun onDetach() {
        val fragmentManager = activity?.supportFragmentManager
        act.fragmentTransaction {
            fragmentManager!!.findFragmentByTag(FragmentTags.CATEGORIES)?.let { show(it) }
            setReorderingAllowed(true)
        }
        super.onDetach()
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.setHasFixedSize(true)

        viewModel.observeData(playlistType)
            .subscribe(this, adapter::submitList)

        viewModel.observeSelectedCount()
            .subscribe(viewLifecycleOwner) { size ->
                val text = when (size) {
                    0 -> getString(R.string.playlist_tracks_chooser_no_tracks)
                    else -> resources.getQuantityString(R.plurals.playlist_tracks_chooser_count, size, size)
                }
                header.text = text
                save.toggleVisibility(size > 0, true)
            }

//        viewModel.getAllSongs(playlistType, filter(view))
//                .subscribe(viewLifecycleOwner) {
//                    view.sidebar.onDataChanged(it) TODO
//                }

//        adapter.setAfterDataChanged({
//            view.emptyStateText.toggleVisibility(it.isEmpty(), true)
//        }) TODO

        RxView.clicks(view.back)
            .asLiveData()
            .subscribe(viewLifecycleOwner) {
                ImeUtils.hideIme(filter)
                act.onBackPressed()
            }

        RxView.clicks(view.save)
            .asLiveData()
            .subscribe(viewLifecycleOwner) { showCreateDialog() }

        RxView.clicks(view.filterList)
            .asLiveData()
            .subscribe(viewLifecycleOwner) {
                if (viewModel.toggleShowOnlyFiltered()) {
                    view.filterList.toggleSelected()

                    toast?.cancel()

                    if (view.filterList.isSelected) {
                        toast = act.toast(R.string.playlist_tracks_chooser_show_only_selected)
                    } else {
                        toast = act.toast(R.string.playlist_tracks_chooser_show_all)
                    }
                } else {
                    act.toast("No song selected")
                }
            }

        RxTextView.afterTextChangeEvents(view.filter)
            .map { it.editable().toString() }
            .filter { it.isBlank() || it.trim().length >= 2 }
            .debounceFirst(250, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .asLiveData()
            .subscribe(this, viewModel::updateFilter)

        RxTextView.afterTextChangeEvents(view.filter)
            .map { it.view().text.isBlank() }
            .asLiveData()
            .subscribe(viewLifecycleOwner) { isEmpty ->
                view.clear.toggleVisibility(!isEmpty, true)
            }

        view.sidebar.scrollableLayoutId = R.layout.item_choose_track
    }

    override fun onResume() {
        super.onResume()
//        sidebar.setListener(letterTouchListener) TODO
        clear.setOnClickListener { filter.setText("") }
    }

    override fun onPause() {
        super.onPause()
        sidebar.setListener(null)
        clear.setOnClickListener(null)
    }

    private fun showCreateDialog() {
        val builder = ThemedDialog.builder(act)
            .setTitle("New playlist")
            .setView(R.layout.layout_edit_text)
            .setPositiveButton(R.string.common_ok, null)
            .setNegativeButton(R.string.common_cancel, null)

        val dialog = builder.show()

        val editText = dialog.findViewById<TextInputEditText>(R.id.editText)!!
        val editTextLayout = dialog.findViewById<TextInputLayout>(R.id.editTextLayout)!!
        val clearButton = dialog.findViewById<View>(R.id.clear)!!
        clearButton.setOnClickListener { editText.setText("") }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val editTextString = editText.text.toString()
            when {
                editTextString.isBlank() -> showError(editTextLayout, "Playlist name not valid")
                else -> {
                    viewModel.savePlaylist(playlistType, editTextString,
                        onSuccess = {
                            dialog.dismiss()
                            act.onBackPressed()
                        },
                        onFail = {
                            ctx.toast("Something wen wrong")
                        })

                }
            }
        }

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        errorDisposable.unsubscribe()
    }

    private fun showError(editTextLayout: TextInputLayout, error: String) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        editTextLayout.startAnimation(shake)
        editTextLayout.error = error
        editTextLayout.isErrorEnabled = true

        errorDisposable.unsubscribe()
        errorDisposable = Single.timer(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ editTextLayout.isErrorEnabled = false }, Throwable::printStackTrace)
    }

//    private val letterTouchListener = WaveSideBarView.OnTouchLetterChangeListener { letter -> TODO
//        list.stopScroll()
//
//        val position = when (letter){
//            TextUtils.MIDDLE_DOT -> -1
//            "#" -> 0
//            "?" -> adapter.itemCount - 1
//            else -> adapter.indexOf {
//                if (it.title.isBlank()) false
//                else it.title[0].toUpperCase().toString() == letter
//            }
//        }
//        if (position != -1){
//            val layoutManager = list.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPositionWithOffset(position, 0)
//        }
//    }

    override fun provideLayoutId(): Int = R.layout.fragment_playlist_track_chooser
}