package dev.olog.presentation.dialog_entry

import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import io.reactivex.Completable
import javax.inject.Inject

class PlaylistMenuListener @Inject constructor(
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator

) : BaseMenuListener(getSongListByParamUseCase, navigator) {

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId){
            R.id.rename -> {
                Completable.fromCallable { navigator.toRenameDialog(mediaId) }
                        .subscribe()
            }
        }
        return super.onMenuItemClick(item)
    }

}
