package dev.olog.msc.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.home.domain.IsRepositoryEmptyUseCase
import dev.olog.msc.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) : ViewModel() {

    private var disposable: Disposable? = null
    private val liveData = MutableLiveData<Boolean>()

    init {
//        TODO
//        disposable = isRepositoryEmptyUseCase.execute()
//            .subscribe({
//                liveData.value = it
//            }, Throwable::printStackTrace)
    }

    fun observeIsRepositoryEmpty() : LiveData<Boolean> = liveData

    fun isFirstAccess(): Boolean {
        return appPreferencesUseCase.isFirstAccess()
    }

    fun getLastBottomViewPage(): Int = appPreferencesUseCase.getLastBottomViewPage()

    fun setLastBottomViewPage(page: Int) {
        appPreferencesUseCase.setLastBottomViewPage(page)
    }

    fun canShowPodcastCategory(): Boolean {
        return appPreferencesUseCase.canShowPodcastCategory()
    }

    override fun onCleared() {
        disposable.unsubscribe()
    }

}