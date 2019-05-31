package dev.olog.msc.appshortcuts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import dev.olog.msc.presentation.navigator.Services
import dev.olog.msc.shared.MusicConstants

class ShortcutsActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO test
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        handleIntent(intent!!)
        finish()
    }

    private fun handleIntent(intent: Intent){
        val action = intent.action
        when (action){
            MusicConstants.ACTION_PLAY -> {
                val serviceIntent = Intent(this, Services.music())
                serviceIntent.action = MusicConstants.ACTION_PLAY
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            MusicConstants.ACTION_SHUFFLE -> {
                val serviceIntent = Intent(this, Services.music())
                serviceIntent.action = MusicConstants.ACTION_SHUFFLE
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
    }

}