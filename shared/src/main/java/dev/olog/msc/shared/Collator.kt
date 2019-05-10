package dev.olog.msc.shared

import java.text.Collator
import java.util.*

val collator : Collator by lazy {
    val instance = Collator.getInstance(Locale.UK)
    instance.strength = Collator.SECONDARY
//        instance.decomposition = Collator.CANONICAL_DECOMPOSITION
    instance
}