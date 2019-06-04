package dev.olog.msc.presentation.popup

import android.app.Activity
import android.view.View
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.app.injection.CoreComponent
import dev.olog.msc.core.MediaId
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.presentation.popup.di.DaggerPopupComponent
import dev.olog.msc.presentation.popup.main.MainPopupDialog
import dev.olog.msc.shared.interfaces.IPopupFacade
import javax.inject.Inject

class PopupFacade : IPopupFacade {

    @Inject internal lateinit var mainPopup: MainPopupDialog
    @Inject internal lateinit var itemPopup: PopupMenuFactory

    init {
        DaggerPopupComponent.factory()
            .create(CoreComponent.safeCoreComponent())
            .inject(this)
    }

    override fun main(activity: Activity, anchor: View, stringMediaIdCategory: String?) {
        val mediaIdCategory = stringMediaIdCategory?.let { MediaIdCategory.valueOf(it) }
        mainPopup.show(activity as FragmentActivity, anchor, mediaIdCategory)
    }

    override fun item(anchor: View, stringMediaId: String) {
        val mediaId = MediaId.fromString(stringMediaId)
        itemPopup.show(anchor, mediaId)
    }
}