package dev.olog.msc.presentation.equalizer

object EqHelper {

    val minDB = -15f
    val maxDB = 15f

    fun projectY(dB: Float): Float {
        val pos = (dB - minDB) / (maxDB - minDB)
        return (1 - pos)
    }

}
