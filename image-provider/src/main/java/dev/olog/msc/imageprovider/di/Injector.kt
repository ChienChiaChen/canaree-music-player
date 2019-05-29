package dev.olog.msc.imageprovider.di

import android.app.Application
import android.content.Context
import dev.olog.msc.app.injection.AppComponent
import dev.olog.msc.imageprovider.glide.GlideModule

fun GlideModule.inject(context: Context){
    DaggerImageProviderComponent.factory()
        .create(AppComponent.appComponent(context.applicationContext as Application))
        .inject(this)
}