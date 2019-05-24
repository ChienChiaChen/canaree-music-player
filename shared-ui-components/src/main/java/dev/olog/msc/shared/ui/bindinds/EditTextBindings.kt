package dev.olog.msc.shared.ui.bindinds

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import dev.olog.msc.shared.core.channel.asFlow
import dev.olog.msc.shared.utils.assertMainThread
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

suspend fun EditText.afterTextChange(): Flow<String> {
    assertMainThread()
    val channel = Channel<String>(Channel.CONFLATED)

    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            GlobalScope.launch {
                if (!channel.isClosedForSend) {
                    channel.send(s!!.toString())
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    addTextChangedListener(watcher)
    channel.invokeOnClose {
        assertMainThread()
        removeTextChangedListener(watcher)
    }
    return channel.asFlow()
}