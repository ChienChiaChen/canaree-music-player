package dev.olog.msc.core.entity.data.request

class Filter(
    word: String,
    val byColumn: Array<By>,
    val behaviorOnEmpty: BehaviorOnEmpty = BehaviorOnEmpty.ALL
) {

    val word = word.trim()

    companion object {
        val NO_FILTER = Filter("", arrayOf())
    }

    enum class By {
        TITLE,
        ARTIST,
        ALBUM
    }

    enum class BehaviorOnEmpty {
        NONE, ALL
    }

    fun with(word: String? = null, byColumn: Array<By>? = null, behaviorOnEmpty: BehaviorOnEmpty? = null): Filter {
        return Filter(word ?: this.word, byColumn ?: this.byColumn, behaviorOnEmpty ?: this.behaviorOnEmpty)
    }

}