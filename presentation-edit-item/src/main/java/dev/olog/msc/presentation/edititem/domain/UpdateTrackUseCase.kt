package dev.olog.msc.presentation.edititem.domain

import android.content.Context
import dev.olog.msc.core.coroutines.CompletableFlowWithParam
import dev.olog.msc.core.coroutines.ComputationDispatcher
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.presentation.edititem.utils.notifyItemChanged
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    schedulers: ComputationDispatcher,
    private val gateway: UsedImageGateway

) : CompletableFlowWithParam<UpdateTrackUseCase.Data>(schedulers) {

    override suspend fun buildUseCaseObservable(param: Data) {
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
                } catch (ex: Throwable) {
                    ex.printStackTrace()
                }
            }

            audioFile.commit()

            if (param.id != null) {
                gateway.setForTrack(param.id, param.image)
            }

            notifyItemChanged(context, param.path)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    data class Data(
        val id: Long?,
        val path: String,
        val image: String?,
        val fields: Map<FieldKey, String>
    )

}