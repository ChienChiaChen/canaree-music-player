package dev.olog.msc.presentation.library.categories.track

import android.content.Context
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import dev.olog.msc.R
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.asString
import dev.olog.msc.presentation.library.folder.tree.FolderTreeFragment
import dev.olog.msc.presentation.library.tab.TabFragment

class CategoriesViewPager(
        private val context: Context,
        fragmentManager: FragmentManager,
        private val categories : List<LibraryCategoryBehavior>

) : FragmentPagerAdapter(fragmentManager) {

    fun getCategoryAtPosition(position: Int): MediaIdCategory? {
        try {
            return categories[position].category
        } catch (ex: Exception){
            return null
        }
    }

    override fun getItem(position: Int): Fragment {
        val category = categories[position].category

        return if (category == MediaIdCategory.FOLDERS && showFolderAsHierarchy()){
            FolderTreeFragment.newInstance()
        } else TabFragment.newInstance(category)
    }

    private fun showFolderAsHierarchy(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return prefs.getBoolean(context.getString(R.string.prefs_folder_tree_view_key), false)
    }

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence? {
        return categories[position].asString(context)
    }

    fun isEmpty() = categories.isEmpty()

}