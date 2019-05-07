package dev.olog.msc.presentation.edititem.domain

import android.content.Context
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.msc.presentation.edititem.utils.notifyItemChanged
import io.reactivex.Completable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
        @ApplicationContext private val context: Context,
        schedulers: IoScheduler,
        private val gateway: UsedImageGateway

) : CompletableUseCaseWithParam<UpdateTrackUseCase.Data>(schedulers) {

    override fun buildUseCaseObservable(param: Data): Completable {
        return Completable.create {
            try {
                val file = File(param.path)
                val audioFile = AudioFileIO.read(file)
                val tag = audioFile.tagOrCreateAndSetDefault
                try {
                    tag.setEncoding("UTF-8")
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                for (field in param.fields) {
                    try {
                        tag.setField(field.key, field.value)
                    } catch (ex: Throwable){
                        ex.printStackTrace()
                    }
                }

                audioFile.commit()

                if (param.id != null) {
                    gateway.setForTrack(param.id, param.image)
                }


                notifyItemChanged(context, param.path)

                it.onComplete()
            } catch (ex: Exception) {
                it.onError(ex)
            }
        }
    }

    data class Data(
            val id: Long?,
            val path: String,
            val image: String?,
            val fields: Map<FieldKey, String>
    )

}