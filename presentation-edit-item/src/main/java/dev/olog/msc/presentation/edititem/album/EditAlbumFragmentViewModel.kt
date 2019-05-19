package dev.olog.msc.presentation.edititem.album

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class EditAlbumFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presenter: EditAlbumFragmentPresenter

) : ViewModel() {

    private val songListLiveData = MutableLiveData<List<Song>>()

    private val displayedAlbum = MutableLiveData<DisplayableAlbum>()

    private var songListDisposable: Disposable? = null
    private var albumDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        albumDisposable = presenter.observeAlbum()
            .subscribe({
                displayedAlbum.postValue(it)
            }, Throwable::printStackTrace)

        songListDisposable = presenter.getSongList()
            .subscribe({
                songListLiveData.postValue(it)
            }, Throwable::printStackTrace)
    }

    fun updateImage(image: String) {
        TODO()
//        val oldValue = displayedAlbum.value!!
//        val newValue = oldValue.copy(image = image)
//        displayedAlbum.postValue(newValue)
    }

    fun getNewImage(): String? {
        TODO()
//        val albumId = getAlbum().id
//        val original = ImagesFolderUtils.forAlbum(context, albumId) // TODO retrieve image from mediametadateretriever
//        val current = displayedAlbum.value!!.image
//        if (original == current){
//            return null
//        } else {
//            return current
//        }
    }

    fun getAlbum(): DisplayableAlbum = presenter.getAlbum()

    fun observeData(): LiveData<DisplayableAlbum> = displayedAlbum

    fun observeSongList(): LiveData<List<Song>> = songListLiveData


    override fun onCleared() {
        songListDisposable.unsubscribe()
    }

}