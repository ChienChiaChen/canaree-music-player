package dev.olog.msc.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.presentation.home.domain.IsPlayingQueueEmptyUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val isRepositoryEmptyUseCase: IsPlayingQueueEmptyUseCase
) : ViewModel() {

    private val liveData = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            isRepositoryEmptyUseCase.execute()
                .collect { liveData.postValue(it) }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
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

}