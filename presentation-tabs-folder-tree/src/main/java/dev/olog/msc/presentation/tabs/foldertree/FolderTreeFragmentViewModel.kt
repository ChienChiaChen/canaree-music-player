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
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.getAll
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.track.FolderGateway
import dev.olog.msc.presentation.tabs.foldertree.utils.isAudioFile
import dev.olog.msc.presentation.tabs.foldertree.utils.isStorageDir
import dev.olog.msc.presentation.tabs.foldertree.utils.safeGetCanonicalFile
import dev.olog.msc.presentation.tabs.foldertree.utils.safeGetCanonicalPath
import dev.olog.msc.shared.core.flow.combineLatest
import dev.olog.msc.shared.extensions.startWith
import dev.olog.msc.shared.extensions.startWithIfNotEmpty
import dev.olog.msc.shared.ui.extensions.liveDataOf
import dev.olog.msc.shared.utils.assertBackgroundThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderGateway

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.folderId("back header")
    }

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            viewModelScope.launch { currentFile.send(currentFile.openSubscription().poll()!!) }
        }
    }

    private val currentFile = BroadcastChannel<File>(Channel.CONFLATED)
    private val currentFileLiveData = liveDataOf<File>()
    private val childrenLiveData = liveDataOf<List<DisplayableFile>>()

    private val defaultFolderLiveData = liveDataOf<Boolean>()

    init {
        viewModelScope.launch { currentFile.send(appPreferencesUseCase.getDefaultMusicFolder()) }
        viewModelScope.launch(Dispatchers.Default) {
            for (file in currentFile.openSubscription()) {
                currentFileLiveData.postValue(file)
                childrenLiveData.postValue(getChildren(file))
            }
        }
        viewModelScope.launch {
            combineLatest(
                appPreferencesUseCase.observeDefaultMusicFolder(),
                currentFile.asFlow()
            ) { default, current -> default.safeGetCanonicalPath() == current.safeGetCanonicalPath() }
                .collect {
                    defaultFolderLiveData.postValue(it)
                }
        }

        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
    }

    override fun onCleared() {
        viewModelScope.cancel()
        context.contentResolver.unregisterContentObserver(observer)
    }

    fun observeFile(): LiveData<File> = currentFileLiveData
    fun observeChildren(): LiveData<List<DisplayableFile>> = childrenLiveData

    private fun getChildren(file: File): List<DisplayableFile> {
        assertBackgroundThread()
        val folderList = gateway.getAll().getAll(Filter.NO_FILTER).map { it.path }
        val children = file.listFiles()
            ?.filter { current -> folderList.firstOrNull { it.contains(current.path) } != null || !current.isDirectory }
            ?: listOf()

        val (directories, files) = children.partition { it.isDirectory }
        val sortedDirectory = filterFolders(directories)
        val sortedFiles = filterTracks(files)

        val displayableItems = sortedDirectory.plus(sortedFiles)

        if (file.path == "/") {
            return displayableItems
        } else {
            return displayableItems.startWith(backDisplableItem)
        }
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
        val current = currentFile.openSubscription().poll()!!
        if (current.path == File.separator) {
            return false
        }

        val parent = current.parentFile
        if (parent?.listFiles() == null || parent.listFiles().isEmpty()) {
            return false
        }
        try {
            nextFolder(current.parentFile)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun goBack() {
        val file = currentFile.openSubscription().poll()!!
        if (!file.isStorageDir()) {
            nextFolder(file.parentFile)
            return
        }
        val parent = file.parentFile
        if (parent.listFiles()?.isNotEmpty() == true) {
            nextFolder(parent)
        }
    }

    fun nextFolder(file: File) {
        viewModelScope.launch { currentFile.send(file) }
    }

    fun observeDefaultFolder(): LiveData<Boolean> = defaultFolderLiveData

    fun updateDefaultFolder() {
        val currentFolder = currentFile.openSubscription().poll()!!
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