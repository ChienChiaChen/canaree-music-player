package dev.olog.msc.presentation.categories.podcast

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.presentation.base.extensions.asString
import dev.olog.msc.presentation.navigator.Fragments

class CategoriesPodcastFragmentViewPager(
    private val context: Context,
    fragmentManager: androidx.fragment.app.FragmentManager,
    private val categories: List<LibraryCategoryBehavior>

) : FragmentPagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): MediaIdCategory? {
        try {
            return categories[position].category
        } catch (ex: Exception) {
            return null
        }
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category
        return Fragments.tab(context, category)
    }

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].asString(context)
    }

    fun isEmpty() = categories.isEmpty()
}