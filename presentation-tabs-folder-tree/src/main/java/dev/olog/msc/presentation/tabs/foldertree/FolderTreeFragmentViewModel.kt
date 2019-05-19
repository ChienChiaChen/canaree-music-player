package dev.olog.msc.presentation.tabs.foldertree

import android.annotation.SuppressLint
import android.content.Context
import android.database.ContentObserver
import android.database.CursorIndexOutOfBoundsException
import android.os.Handler
import android.os.Looper
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.ObserveAllFoldersUseCase
import dev.olog.msc.presentation.base.extensions.asLiveData
import dev.olog.msc.shared.extensions.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asObservable
import java.io.File
import javax.inject.Inject

class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val getAllFoldersUseCase: ObserveAllFoldersUseCase

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.folderId("back header")
    }

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            currentFile.onNext(currentFile.value!!)
        }
    }

    private val currentFile = BehaviorSubject.createDefault(appPreferencesUseCase.getDefaultMusicFolder())

    init {
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
    }

    override fun onCleared() {
        context.contentResolver.unregisterContentObserver(observer)
    }

    fun observeFileName(): LiveData<File> = currentFile
        .asLiveData()

    fun observeChildrens(): LiveData<List<DisplayableFile>> = runBlocking {
        currentFile.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap { file ->
                runBlocking { getAllFoldersUseCase.execute().asObservable() }.mapToList { it.path }.map { folderList ->
                    val children = file.listFiles()
                        ?.filter { current -> folderList.firstOrNull { it.contains(current.path) } != null || !current.isDirectory }
                        ?: listOf()

                    val (directories, files) = children.partition { it.isDirectory }
                    val sortedDirectory = filterFolders(directories)
                    val sortedFiles = filterTracks(files)

                    val displayableItems = sortedDirectory.plus(sortedFiles)

                    if (file.path == "/") {
                        displayableItems
                    } else {
                        displayableItems.startWith(backDisplableItem)
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .asLiveData()
    }

    private fun filterFolders(files: List<File>): List<DisplayableFile> {
        return files.asSequence()
            .filter { it.isDirectory }
            .sortedBy { it.name }
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(foldersHeader)
    }

    private fun filterTracks(files: List<File>): List<DisplayableFile> {
        return files.asSequence()
            .filter { it.isAudioFile() }
            .sortedBy { it.name }
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(tracksHeader)
    }

    fun popFolder(): Boolean {
        val current = currentFile.value!!
        if (current == File(File.separator)) {
            return false
        }

        val parent = current.parentFile
        if (parent?.listFiles() == null || parent.listFiles().isEmpty()) {
            return false
        }
        try {
            currentFile.onNext(current.parentFile)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun goBack() {
        val file = currentFile.value!!
        if (!file.isStorageDir()) {
            currentFile.onNext(file.parentFile)
            return
        }
        val parent = file.parentFile
        if (parent.listFiles()?.isNotEmpty() == true) {
            currentFile.onNext(parent)
        }
    }

    fun nextFolder(file: File) {
        currentFile.onNext(file)
    }

    fun observeCurrentFolder(): Observable<Boolean> = Observables.combineLatest(
        appPreferencesUseCase.observeDefaultMusicFolder(),
        currentFile
    ) { default, current -> default.safeGetCanonicalPath() == current.safeGetCanonicalPath() }

    fun updateDefaultFolder() {
        val currentFolder = currentFile.value!!
        appPreferencesUseCase.setDefaultMusicFolder(currentFolder.safeGetCanonicalFile())
    }

    @SuppressLint("Recycle")
    fun createMediaId(item: DisplayableFile): MediaId? {
        try {
            // TODO move out the query from presentation module
            val file = item.asFile()
            val path = file.path
            val folderMediaId = MediaId.folderId(path.substring(0, path.lastIndexOf(File.separator)))

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(file.path), null
            )?.let { cursor ->

                cursor.moveToFirst()
                val trackId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                cursor.close()
                return MediaId.playableItem(folderMediaId, trackId)
            }
        } catch (ex: CursorIndexOutOfBoundsException) {
            ex.printStackTrace()
        }
        return null
    }

    private val backDisplableItem: List<DisplayableFile> = listOf(
        DisplayableFile(R.layout.item_folder_tree_directory, BACK_HEADER_ID, "...", null, null)
    )

    private val foldersHeader = DisplayableFile(
        R.layout.item_folder_tree_header,
        MediaId.headerId("folder header"),
        context.getString(R.string.common_folders),
        null,
        null
    )

    private val tracksHeader = DisplayableFile(
        R.layout.item_folder_tree_header,
        MediaId.headerId("track header"),
        context.getString(R.string.common_tracks),
        null,
        null
    )

    private fun File.toDisplayableItem(): DisplayableFile {
        val isDirectory = this.isDirectory
        val id = if (isDirectory) R.layout.item_folder_tree_directory else R.layout.item_folder_tree_track

        return DisplayableFile(
            type = id,
            mediaId = MediaId.folderId(this.path),
            title = this.name,
            subtitle = null,
            path = this.path
        )
    }
}