package dev.olog.msc.core.entity

private const val UNKNOWN = "<unknown>"

data class LastMetadata(
        val title: String,
        val subtitle: String,
        val image: String,
        val id: Long
) {

    fun isNotEmpty(): Boolean {
        return title.isNotBlank()
    }

    val description: String
        get() {
            if (subtitle == UNKNOWN){
                return title
            }
            return "$title $subtitle"
        }

}