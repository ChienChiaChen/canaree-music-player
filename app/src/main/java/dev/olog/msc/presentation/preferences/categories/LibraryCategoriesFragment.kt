package dev.olog.msc.presentation.preferences.categories

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import dev.olog.msc.R
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.shared.ui.ThemedDialog
import dev.olog.presentation.base.drag.TouchHelperAdapterCallback
import dev.olog.presentation.base.extensions.ctx
import dev.olog.presentation.base.extensions.withArguments
import javax.inject.Inject

class LibraryCategoriesFragment : BaseDialogFragment() {

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
    private lateinit var adapter: LibraryCategoriesFragmentAdapter

    private val category by lazyFast { MediaIdCategory.values()[arguments!!.getInt(TYPE)] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.dialog_list, null, false)

        val title = if (category == MediaIdCategory.SONGS) R.string.prefs_library_categories_title
                    else R.string.prefs_library_categories_title_podcasts

        val builder = ThemedDialog.builder(ctx)
                .setTitle(title)
                .setView(view)
                .setNeutralButton(R.string.popup_neutral_reset, null)
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_save, null)

        val list = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)
        adapter = LibraryCategoriesFragmentAdapter(presenter.getDataSet(category).toMutableList())
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