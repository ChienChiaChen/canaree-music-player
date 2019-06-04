package dev.olog.msc.presentation.preferences.settings

import android.content.Context
import androidx.lifecycle.LiveData
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.imageprovider.ImagesFolderUtils
import dev.olog.msc.imageprovider.glide.GlideApp
import dev.olog.msc.presentation.base.OnSuccess
import dev.olog.msc.pro.IBilling
import dev.olog.msc.shared.core.coroutines.CustomScope
import dev.olog.msc.shared.ui.extensions.liveDataOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SettingsFragmentPresenter(
    private val context: Context,
    private val billing: IBilling,
    private val tutorialPrefsUseCase: TutorialPreferenceGateway
) : CoroutineScope by CustomScope() {

    private val isPremiumLiveData = liveDataOf<Boolean>()

    fun onAttach() {
        launch {
            billing.observeIsPremium()
                .take(2)
                .distinctUntilChanged()
                .collect { isPremiumLiveData.postValue(it) }
        }
    }

    fun onDetach() {
        cancel()
    }

    fun observeIsPremium(): LiveData<Boolean> = isPremiumLiveData

    fun purchasePremium() {
        billing.purchasePremium()
    }

    fun clearCachedImages(onSuccess: OnSuccess) = launch(Dispatchers.IO) {
        GlideApp.get(context).clearDiskCache()

        ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.FOLDER).listFiles()
            .forEach { it.delete() }
        ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.PLAYLIST).listFiles()
            .forEach { it.delete() }
        ImagesFolderUtils.getImageFolderFor(context, ImagesFolderUtils.GENRE).listFiles()
            .forEach { it.delete() }
        onSuccess()
    }

    fun resetTutorial(){
        tutorialPrefsUseCase.reset()
    }

}