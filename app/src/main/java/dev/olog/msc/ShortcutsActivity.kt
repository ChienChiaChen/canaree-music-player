package dev.olog.msc

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.olog.msc.musicservice.MusicService
import dev.olog.msc.shared.MusicConstants

class ShortcutsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MusicConstants.ACTION_PLAY
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            MusicConstants.ACTION_SHUFFLE -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MusicConstants.ACTION_SHUFFLE
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
    }

}