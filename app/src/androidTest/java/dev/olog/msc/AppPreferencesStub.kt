package dev.olog.msc

import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import java.io.File

class AppPreferencesStub : AppPreferencesGateway {

    override fun getLastBottomViewPage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastBottomViewPage(page: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isFirstAccess(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getVisibleTabs(): BooleanArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getViewPagerLibraryLastPage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setViewPagerLibraryLastPage(lastPage: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getViewPagerPodcastLastPage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setViewPagerPodcastLastPage(lastPage: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLibraryCategories(): List<LibraryCategoryBehavior> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultLibraryCategories(): List<LibraryCategoryBehavior> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultPodcastLibraryCategories(): List<LibraryCategoryBehavior> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBlackList(): Set<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBlackList(set: Set<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resetSleepTimer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSleepTimer(sleepFrom: Long, sleepTime: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSleepTime(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSleepFrom(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observePlayerControlsVisibility(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDefault(): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canAutoCreateImages(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastFmCredentials(): UserCredentials {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun observeLastFmCredentials(): Flow<UserCredentials> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLastFmCredentials(user: UserCredentials) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSyncAdjustment(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSyncAdjustment(value: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeDefaultMusicFolder(): Observable<File> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultMusicFolder(): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDefaultMusicFolder(file: File) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canShowLibraryNewVisibility(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canShowLibraryRecentPlayedVisibility(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canShowPodcastCategory(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAdaptiveColorEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeLockscreenArtworkEnabled(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getShowFolderAsTreeView(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}