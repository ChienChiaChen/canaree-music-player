package dev.olog.msc.presentation.edititem.track

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
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.C
import kotlinx.android.synthetic.main.fragment_edit_track.*
import javax.inject.Inject

class EditTrackFragment : BaseEditItemFragment() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditTrackFragment {
            return EditTrackFragment().withArguments(
                ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<EditTrackFragmentViewModel>(viewModelFactory) }
    private val editItemViewModel by lazyFast { activity!!.viewModelProvider<EditItemViewModel>(viewModelFactory) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.observeData().observe(this, Observer {
            if (it == null) {
                ctx.toast(R.string.edit_song_info_not_found)
            } else {
                title.setText(it.title)
                artist.setText(it.artist)
                albumArtist.setText(it.albumArtist)
                album.setText(it.album)
                year.setText(it.year)
                genre.setText(it.genre)
                disc.setText(it.disc)
                trackNumber.setText(it.track)
                bitrate.text = it.bitrate
                format.text = it.format
                sampling.text = it.sampling
                setImage(MediaId.songId(it.id))
            }
            hideLoader()
        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
//        RxTextView.afterTextChangeEvents(view.title)
//            .map { it.view().text.toString() }
//            .map { it.isNotBlank() }
//            .asLiveData()
//            .subscribe(viewLifecycleOwner, view.okButton::setEnabled)
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            val result = editItemViewModel.updateSong(
                UpdateSongInfo(
                    viewModel.getSong(),
                    title.extractText().trim(),
                    artist.extractText().trim(),
                    albumArtist.extractText().trim(),
                    album.extractText().trim(),
                    genre.extractText().trim(),
                    year.extractText().trim(),
                    disc.extractText().trim(),
                    trackNumber.extractText().trim(),
                    viewModel.getNewImage()
                )
            )

            when (result) {
                UpdateResult.OK -> dismiss()
                UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                UpdateResult.ILLEGAL_DISC_NUMBER -> ctx.toast(R.string.edit_song_invalid_disc_number)
                UpdateResult.ILLEGAL_TRACK_NUMBER -> ctx.toast(R.string.edit_song_invalid_track_number)
                UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
            }
        }
        cancelButton.setOnClickListener { dismiss() }
        autoTag.setOnClickListener {
            if (viewModel.fetchSongInfo()) {
                showLoader(R.string.edit_song_fetching_info)
            } else {
                ctx.toast(R.string.common_no_internet)
            }
        }
        picker.setOnClickListener { changeImage() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        picker.setOnClickListener(null)
    }

    override fun onImagePicked(uri: Uri) {
        viewModel.updateImage(uri.toString())
    }

    override fun restoreImage() {
        TODO()
//        val albumId = viewModel.getSong().albumId
//        val uri = ImagesFolderUtils.forAlbum(requireContext(), albumId)
//        viewModel.updateImage(uri)
    }

    override fun noImage() {
        viewModel.updateImage(C.NO_IMAGE)
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetching()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}