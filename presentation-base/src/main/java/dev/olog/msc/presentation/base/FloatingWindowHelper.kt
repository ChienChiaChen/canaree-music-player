package dev.olog.msc.presentation.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dev.olog.msc.pro.HasBilling

object FloatingWindowHelper {

    const val REQUEST_CODE_HOVER_PERMISSION = 1000

    @SuppressLint("NewApi")
    fun startServiceOrRequestOverlayPermission(activity: Activity, floatingWindowClass: Class<*>){
        val billing = (activity as HasBilling).billing
        if (billing.isPremium()){
            if (hasOverlayPermission(activity)){
                val intent = Intent(activity, floatingWindowClass)
                ContextCompat.startForegroundService(activity, intent)
            } else {
                val intent = createIntentToRequestOverlayPermission(activity)
                activity.startActivityForResult(intent, REQUEST_CODE_HOVER_PERMISSION)
            }
        } else {
            billing.purchasePremium()
        }
    }

    @SuppressLint("NewApi")
    fun startServiceIfHasOverlayPermission(activity: Activity, floatingWindowClass: Class<*>){
        val billing = (activity as HasBilling).billing

        if (billing.isPremium() && hasOverlayPermission(activity)){
            val intent = Intent(activity, floatingWindowClass)
            ContextCompat.startForegroundService(activity, intent)
        }
    }

    @CheckResult
    private fun hasOverlayPermission(context: Context): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Runtime permissions are required. Check for the draw overlay permission.
            Settings.canDrawOverlays(context)
        } else {
            // No runtime permissions required. We're all good.
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @CheckResult
    private fun createIntentToRequestOverlayPermission(context: Context): Intent {
        return Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
        )
    }

}