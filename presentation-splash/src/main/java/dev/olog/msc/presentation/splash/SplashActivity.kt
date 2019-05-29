package dev.olog.msc.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dev.olog.msc.presentation.base.activity.ThemedActivity
import dev.olog.msc.shared.Permissions
import dev.olog.msc.shared.core.lazyFast
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity(),
    View.OnClickListener,
    ThemedActivity{

    private val adapter by lazyFast { SplashActivityViewPagerAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewPager.adapter = adapter
        inkIndicator.setViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()
        next.setOnClickListener {
            if (viewPager.currentItem == 0){
                viewPager.setCurrentItem(1, true)
            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        next.setOnClickListener(null)
    }

    override fun onClick(v: View?) {
        if (viewPager.currentItem == 0){
            viewPager.setCurrentItem(1, true)
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission(){
        if (!Permissions.canReadStorage(this)){
            Permissions.requestReadStorage(this)
        } else {
            onStoragePermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Permissions.checkWriteCode(requestCode)){
            if (Permissions.canReadStorage(this)){
                onStoragePermissionGranted()
            } else {
                onStoragePermissionDenied()
            }
        }
    }

    private fun onStoragePermissionGranted(){
        ExplainTrialDialog.show(this) {
            finishActivity()
        }
    }

    private fun finishActivity(){
        finish()
    }


    private fun onStoragePermissionDenied(){
        if (Permissions.hasUserDisabledReadStorage(this)){
            AlertDialog.Builder(this)
                .setTitle(R.string.splash_storage_permission)
                .setMessage(R.string.splash_storage_permission_disabled)
                .setPositiveButton(R.string.common_ok) { _, _ -> toSettings() }
                .setNegativeButton(R.string.common_no, null)
                .show()
        }
    }

    private fun toSettings(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
        startActivity(intent)
    }

}