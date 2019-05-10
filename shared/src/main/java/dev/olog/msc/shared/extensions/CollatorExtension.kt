@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.msc.shared.extensions

import dev.olog.msc.shared.TrackUtils
import java.text.Collator

fun Collator.safeCompare(source: String, target: String): Int {
    val s = if (source == TrackUtils.UNKNOWN) source.substring(1) else source
    val t = if (target == TrackUtils.UNKNOWN) target.substring(1) else target
    return this.compare(s, t)
}