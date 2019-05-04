package dev.olog.msc.musicservice

import dev.olog.msc.core.dagger.scope.PerService
import dev.olog.msc.musicservice.interfaces.PlayerLifecycle
import dev.olog.msc.musicservice.model.MediaEntity
import dev.olog.msc.shared.extensions.removeFirst
import javax.inject.Inject

@PerService
class EnhancedShuffle @Inject constructor(
        playerLifecycle: PlayerLifecycle

)  {

    private var lastListened = mutableListOf<Long>()

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onMetadataChanged(entity: MediaEntity) {
            lastListened.removeFirst { it == entity.id }
            lastListened.add(0, entity.id)
        }
    }

    init {
        playerLifecycle.addListener(playerListener)
    }

    fun shuffle(list: MutableList<MediaEntity>): List<MediaEntity>{
        val halfListSize = list.size / 2

        lastListened = lastListened.take(halfListSize).toMutableList()

        val shuffled = list.shuffled().toMutableList()

        for (l in lastListened.reversed()) {
            val index = shuffled.indexOfFirst { it.id == l }
            if (index in 0..halfListSize){
                val item = shuffled[index]
                shuffled.removeAt(index)
                shuffled.add(item)
            }
        }

        return shuffled
    }

}