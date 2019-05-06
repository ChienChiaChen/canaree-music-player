package dev.olog.msc.presentation.preferences.blacklist

import dev.olog.msc.R
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.entity.track.Folder
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.interactor.all.GetAllFoldersUnfiltered
import dev.olog.msc.shared.extensions.mapToList
import dev.olog.presentation.base.model.DisplayableItem
import io.reactivex.Observable
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
        getAllFoldersUnfiltered: GetAllFoldersUnfiltered,
        private val appPreferencesUseCase: AppPreferencesGateway
) {

    val data : Observable<List<BlacklistModel>> = getAllFoldersUnfiltered.execute()
            .mapToList { it.toDisplayableItem() }
            .map { folders ->
                val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase() }
                folders.map { BlacklistModel(it, blacklisted.contains(it.subtitle!!.toLowerCase())) }
            }

    private fun Folder.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.dialog_blacklist_item,
                MediaId.folderId(this.path),
                this.title,
                this.path,
                this.image
        )
    }

    fun setDataSet(data: List<BlacklistModel>){
        val blacklisted = data.filter { it.isBlacklisted }
                .mapNotNull { it.displayableItem.subtitle }
                .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
    }


}

class BlacklistModel(
        val displayableItem: DisplayableItem,
        var isBlacklisted: Boolean
)