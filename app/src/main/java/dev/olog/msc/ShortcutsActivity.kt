package dev.olog.msc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager

class ShortcutsActivity : Activity(){

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
//        val action = intent.action TODO
//        when (action){
//            MusicConstants.ACTION_PLAY -> {
//                val serviceIntent = Intent(this, MusicService::class.java)
//                serviceIntent.action = MusicConstants.ACTION_PLAY
//                ContextCompat.startForegroundService(this, serviceIntent)
//            }
//            MusicConstants.ACTION_SHUFFLE -> {
//                val serviceIntent = Intent(this, MusicService::class.java)
//                serviceIntent.action = MusicConstants.ACTION_SHUFFLE
//                ContextCompat.startForegroundService(this, serviceIntent)
//            }
//        }
    }

}