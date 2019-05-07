package dev.olog.msc.presentation.edititem.track

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.presentation.edititem.DisplayableSong
import dev.olog.msc.shared.extensions.unsubscribe
import dev.olog.msc.shared.utils.NetworkUtils
import io.reactivex.disposables.Disposable
import org.jaudiotagger.tag.TagOptionSingleton
import javax.inject.Inject

class EditTrackFragmentViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        private val presenter: EditTrackFragmentPresenter

) : ViewModel() {

    private val displayedSong = MutableLiveData<DisplayableSong>()

    private var getSongDisposable : Disposable? = null
    private var fetchSongInfoDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        getSongDisposable = presenter.observeSong()
                .subscribe({ song ->
                    displayedSong.postValue(song)
                }, {
                    it.printStackTrace()
                })
    }

    fun updateImage(image: String){
        val oldValue = displayedSong.value
        val newValue = oldValue?.copy(image = image)
        displayedSong.postValue(newValue)
    }

    fun getNewImage(): String? {
        try {
            val albumId = getSong().albumId
            val original = ImagesFolderUtils.forAlbum(context, albumId)
            val current = displayedSong.value!!.image
            if (original == current){
                return null
            } else {
                return current
            }
        } catch (ex: KotlinNullPointerException){
            return null
        }

    }

    fun observeData(): LiveData<DisplayableSong> = displayedSong
    fun getSong(): DisplayableSong = presenter.getSong()

    fun fetchSongInfo(): Boolean {
        if (!NetworkUtils.isConnected(context)){
            return false
        }

        fetchSongInfoDisposable.unsubscribe()
        fetchSongInfoDisposable = presenter.fetchData()
                .map { it.get()!! }
                .subscribe({ newValue ->
                    val oldValue = displayedSong.value!!
                    displayedSong.postValue(oldValue.copy(
                            title = newValue.title,
                            artist = newValue.artist,
                            album = newValue.album
                    ))
                }, { throwable ->
                    throwable.printStackTrace()
                    displayedSong.postValue(null)
                })

        return true
    }

    fun stopFetching(){
        fetchSongInfoDisposable.unsubscribe()
    }

    override fun onCleared() {
        getSongDisposable.unsubscribe()
        stopFetching()
    }

}