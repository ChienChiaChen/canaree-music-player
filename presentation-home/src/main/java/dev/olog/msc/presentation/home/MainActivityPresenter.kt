package dev.olog.msc.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.home.domain.IsRepositoryEmptyUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase
) : ViewModel() {

    private val liveData = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            isRepositoryEmptyUseCase.execute()
                .collect { liveData.postValue(it) }
        }
    }

    fun observeIsRepositoryEmpty(): LiveData<Boolean> = liveData

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
        viewModelScope.cancel()
    }

}