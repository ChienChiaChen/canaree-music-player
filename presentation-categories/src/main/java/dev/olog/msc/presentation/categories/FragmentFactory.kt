package dev.olog.msc.presentation.categories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dev.olog.msc.core.Classes
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.FragmentTags
import dev.olog.msc.presentation.base.extensions.withArguments

class FragmentFactory(
    private val factory: FragmentFactory
) {


    fun tabFragment(category: MediaIdCategory): Fragment {
        val fragment = factory.instantiate(
            ClassLoader.getSystemClassLoader(),
            Classes.tabFragment,
            null
        )
        return fragment.withArguments(FragmentTags.TAB_ARGUMENTS_SOURCE to category.ordinal)
    }

    fun folderTreeFragment(): Fragment {
        return factory.instantiate(
            ClassLoader.getSystemClassLoader(),
            Classes.folderTreeFragment,
            null
        )
    }

}