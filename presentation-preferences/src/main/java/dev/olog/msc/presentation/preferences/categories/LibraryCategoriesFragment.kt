package dev.olog.msc.presentation.preferences.categories

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.android.support.AndroidSupportInjection
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.extensions.withArguments
import dev.olog.msc.presentation.preferences.R
import dev.olog.msc.shared.extensions.lazyFast
import javax.inject.Inject

class LibraryCategoriesFragment : DialogFragment() {

    companion object {
        const val TAG = "LibraryCategoriesFragment"
        const val TYPE = "$TAG.TYPE"

        @JvmStatic
        fun newInstance(category: MediaIdCategory): LibraryCategoriesFragment {
            return LibraryCategoriesFragment().withArguments(
                    TYPE to category.ordinal
            )
        }
    }

    @Inject lateinit var presenter: LibraryCategoriesFragmentPresenter
    private val adapter by lazyFast {
        LibraryCategoriesFragmentAdapter(presenter.getDataSet(category).toMutableList())
    }

    private val category by lazyFast { MediaIdCategory.values()[arguments!!.getInt(TYPE)] }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val title = if (category == MediaIdCategory.SONGS) R.string.prefs_library_categories_title
                    else R.string.prefs_library_categories_title_podcasts

        val builder = AlertDialog.Builder(ctx)
                .setTitle(title)
                .setView(view)
                .setNeutralButton(R.string.common_reset, null)
                .setNegativeButton(R.string.common_cancel, null)
                .setPositiveButton(R.string.common_save, null)

        val list = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)
        list.adapter = adapter
        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        val callback = TouchHelperAdapterCallback(adapter, 0)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(list)
        adapter.touchHelper = touchHelper

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
                    val defaultData = presenter.getDefaultDataSet(category)
                    adapter.updateDataSet(defaultData)
                }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    presenter.setDataSet(category, adapter.data)
                    activity!!.setResult(Activity.RESULT_OK)
                    dismiss()
                }

        return dialog
    }

}