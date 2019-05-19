package dev.olog.msc.presentation.edititem.domain

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Page
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.interactor.GetSongListChunkByParamUseCase
import javax.inject.Inject

class UpdateMultipleTracksUseCase @Inject constructor(
    schedulers: ComputationDispatcher,
    private val getSongListByParamUseCase: GetSongListChunkByParamUseCase,
    private val updateTrackUseCase: UpdateTrackUseCase,
    private val gateway: UsedImageGateway

) : CompletableFlowWithParam<UpdateMultipleTracksUseCase.Data>(schedulers) {

    override suspend fun buildUseCaseObservable(param: Data) {
        val songList =
            getSongListByParamUseCase.execute(param.mediaId).getPage(Request(Page.NO_PAGING, Filter.NO_FILTER))
        for (any in songList) {
            if (any is Song) {
                updateTrackUseCase.execute(UpdateTrackUseCase.Data(null, any.path, null, param.fields))
                if (param.mediaId.isArtist || param.mediaId.isPodcastArtist) {
                    gateway.setForArtist(param.mediaId.resolveId, param.image)
                } else if (param.mediaId.isAlbum || param.mediaId.isPodcastAlbum) {
                    gateway.setForAlbum(param.mediaId.resolveId, param.image)
                } else {
                    throw IllegalStateException("invalid media id category ${param.mediaId}")
                }
            } else if (any is Podcast) {
                updateTrackUseCase.execute(UpdateTrackUseCase.Data(null, any.path, null, param.fields))
                if (param.mediaId.isArtist || param.mediaId.isPodcastArtist) {
                    gateway.setForArtist(param.mediaId.resolveId, param.image)
                } else if (param.mediaId.isAlbum || param.mediaId.isPodcastAlbum) {
                    gateway.setForAlbum(param.mediaId.resolveId, param.image)
                } else {
                    throw IllegalStateException("invalid media id category ${param.mediaId}")
                }
            }
        }
    }

    data class Data(
        val mediaId: MediaId,
        val image: String?,
        val fields: Map<FieldKey, String>
    )

}