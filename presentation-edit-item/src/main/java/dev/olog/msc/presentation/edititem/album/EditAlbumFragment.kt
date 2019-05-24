package dev.olog.msc.presentation.edititem.album

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.core.MediaId
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.extensions.extractText
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.edititem.*
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.C
import dev.olog.msc.shared.ui.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_edit_album.*
import javax.inject.Inject

class EditAlbumFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditAlbumFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditAlbumFragment {
            return EditAlbumFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<EditAlbumFragmentViewModel>(
            viewModelFactory
        )
    }
    private val editItemViewModel by lazyFast {
        activity!!.viewModelProvider<EditItemViewModel>(
            viewModelFactory
        )
    }
    @Inject
    lateinit var mediaId: MediaId

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
//        RxTextView.afterTextChangeEvents(view.album)
//            .map { it.view().text.toString() }
//            .map { it.isNotBlank() }
//            .asLiveData()
//            .subscribe(viewLifecycleOwner, view.okButton::setEnabled)

        viewModel.observeSongList()
            .subscribe(viewLifecycleOwner) {
                val size = it.size
                val text = resources.getQuantityString(
                    R.plurals.edit_item_xx_tracks_will_be_updated, size, size
                )
                albumsUpdated.text = text
            }

        viewModel.observeData().observe(viewLifecycleOwner, Observer {
            when (it) {
                null -> ctx.toast(R.string.edit_song_info_not_found)
                else -> {
                    album.setText(it.title)
                    artist.setText(it.artist)
                    albumArtist.setText(it.albumArtist)
                    year.setText(it.year)
                    genre.setText(it.genre)
                    setImage(MediaId.albumId(it.id))
                }
            }
            hideLoader()
        })
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = editItemViewModel.updateAlbum(
                UpdateAlbumInfo(
                    mediaId,
                    album.extractText().trim(),
                    artist.extractText().trim(),
                    albumArtist.extractText().trim(),
                    genre.extractText().trim(),
                    year.extractText().trim(),
                    viewModel.getNewImage()
                )
            )

            when (result) {
                UpdateResult.OK -> dismiss()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
                UpdateResult.ILLEGAL_DISC_NUMBER,
                UpdateResult.ILLEGAL_TRACK_NUMBER -> {
                }
            }
        }
        cancelButton.setOnClickListener { dismiss() }
        picker.setOnClickListener { changeImage() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        picker.setOnClickListener(null)
    }

    override fun restoreImage() {
        viewModel.updateImage("")
    }

    override fun onImagePicked(uri: Uri) {
        viewModel.updateImage(uri.toString())
    }

    override fun noImage() {
        viewModel.updateImage(C.NO_IMAGE)
    }

    override fun onLoaderCancelled() {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_album
}