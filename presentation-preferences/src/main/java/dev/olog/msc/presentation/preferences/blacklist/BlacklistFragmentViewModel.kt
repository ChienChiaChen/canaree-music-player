package dev.olog.msc.presentation.preferences.blacklist


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.ObserveAllFoldersUnfilteredUseCase
import dev.olog.msc.presentation.base.OnSuccess
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.preferences.R
import dev.olog.msc.shared.core.coroutines.mapToList
import dev.olog.msc.shared.ui.extensions.liveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlacklistFragmentViewModel @Inject constructor(
    observeAllFoldersUnfiltered: ObserveAllFoldersUnfilteredUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway
) : ViewModel() {

    private val data = liveDataOf<List<BlacklistModel>>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            observeAllFoldersUnfiltered.execute()
                .mapToList { it.toDisplayableItem() }
                .map { folders ->
                    val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase() } // TODO check
                    folders.map { BlacklistModel(it, blacklisted.contains(it.subtitle!!.toLowerCase())) }
                }.collect {
                    data.postValue(it)
                }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun observeData(): LiveData<List<BlacklistModel>> = data

    private fun Folder.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.dialog_blacklist_item,
            MediaId.folderId(this.path),
            this.title,
            this.path
        )
    }

    fun setDataSet(data: List<BlacklistModel>, onSuccess: OnSuccess) = viewModelScope.launch(Dispatchers.Default) {
        val blacklisted = data.filter { it.isBlacklisted }
            .mapNotNull { it.displayableItem.subtitle }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
        withContext(Dispatchers.Main) {
            onSuccess()
        }
    }


}