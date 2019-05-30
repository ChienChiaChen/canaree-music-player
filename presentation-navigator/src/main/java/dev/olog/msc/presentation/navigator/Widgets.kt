package dev.olog.msc.presentation.navigator

object Widgets {

    private val classMap = mutableMapOf<String, Class<*>>()

    fun colored(): Class<*> {
        return classMap.getOrPut("colored") {
            Class.forName("dev.olog.msc.appwidgets.base.WidgetColored")
        }
    }

    fun all(): List<Class<*>>{
        return listOf(
            colored()
        )
    }

}