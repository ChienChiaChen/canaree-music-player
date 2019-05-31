package dev.olog.msc.presentation.preferences.credentials

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dev.olog.msc.core.entity.UserCredentials
import dev.olog.msc.core.interactor.scrobble.GetLastFmUserCredentials
import dev.olog.msc.core.interactor.scrobble.UpdateLastFmUserCredentials
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.preferences.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO improve
// TODO 1) check if credentials are correft
class LastFmCredentialsFragment : DialogFragment() {

    companion object {
        const val TAG = "LastFmCredentialsFragment"

        @JvmStatic
        fun newInstance(): LastFmCredentialsFragment {
            return LastFmCredentialsFragment()
        }
    }

    @Inject lateinit var getLastFmUserCredentials: GetLastFmUserCredentials
    @Inject lateinit var updateLastFmUserCredentials: UpdateLastFmUserCredentials

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity!!)
        val view : View = inflater.inflate(R.layout.layout_credentials_edit_text, null, false)

        val builder = AlertDialog.Builder(ctx)
                .setTitle(R.string.prefs_last_fm_credentials_title)
                .setMessage(R.string.prefs_last_fm_credentials_message)
                .setView(view)
                .setPositiveButton(R.string.credentials_button_positive, null)
                .setNegativeButton(R.string.credentials_button_negative, null)

        val userName = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)

        val credentials = getLastFmUserCredentials.execute()
        userName.setText(credentials.username)
        password.setText(credentials.password)

        val dialog = builder.show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val user = UserCredentials(
                    userName.text.toString(),
                    password.text.toString()
            )
            GlobalScope.launch { // TODO move to viewmodel
                updateLastFmUserCredentials.execute(user)
                withContext(Dispatchers.Main){
                    dismiss()
                }
            }
        }

        return dialog
    }

}