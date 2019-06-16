package dev.olog.msc.presentation.library.categories.podcast

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.asString
import dev.olog.msc.presentation.library.tab.TabFragment

class CategoriesPodcastFragmentViewPager (
        private val context: Context,
        fragmentManager: androidx.fragment.app.FragmentManager,
        private val categories : List<LibraryCategoryBehavior>

) : FragmentStatePagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): MediaIdCategory? {
        try {
            return categories[position].category
        } catch (ex: Exception){
            return null
        }
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category
        return TabFragment.newInstance(category)
    }

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].asString(context)
    }

    fun isEmpty() = categories.isEmpty()
}