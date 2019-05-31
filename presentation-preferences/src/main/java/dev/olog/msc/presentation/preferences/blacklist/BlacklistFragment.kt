package dev.olog.msc.presentation.preferences.blacklist

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.preferences.R
import dev.olog.msc.presentation.preferences.blacklist.di.inject
import dev.olog.msc.shared.extensions.toast
import dev.olog.msc.shared.ui.extensions.subscribe
import javax.inject.Inject

class BlacklistFragment : DialogFragment() {

    companion object {
        const val TAG = "BlacklistFragment"

        fun newInstance(): BlacklistFragment {
            return BlacklistFragment()
        }
    }

    @Inject
    lateinit var presenter: BlacklistFragmentViewModel
    private lateinit var adapter: BlacklistFragmentAdapter

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.observeData()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view: View = inflater.inflate(R.layout.dialog_list, null, false)

        val builder = AlertDialog.Builder(ctx)
            .setTitle(R.string.prefs_blacklist_title)
            .setMessage(R.string.prefs_blacklist_description)
            .setView(view)
            .setNegativeButton(R.string.common_cancel, null)
            .setPositiveButton(R.string.common_save, null)

        val list = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list)
        adapter = BlacklistFragmentAdapter()
        list.adapter = adapter
        list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val allIsBlacklisted = adapter.data.all { it.isBlacklisted }
            if (allIsBlacklisted) {
                showErrorMessage()
            } else {
                presenter.setDataSet(adapter.data) {
                    notifyMediaStore()
                    dismiss()
                }
            }
        }

        return dialog
    }

    private fun notifyMediaStore() {
        val contentResolver = context!!.contentResolver
        contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
    }

    private fun showErrorMessage() {
        activity!!.toast(R.string.prefs_blacklist_error)
    }

}