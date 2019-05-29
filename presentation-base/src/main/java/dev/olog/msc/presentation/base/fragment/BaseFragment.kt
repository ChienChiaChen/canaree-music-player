package dev.olog.msc.presentation.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.core.gateway.track.SongGateway
import dev.olog.msc.presentation.base.interfaces.HasSlidingPanel
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    // workaround to avoid duplicated classes
    @Inject
    internal lateinit var songGateway: SongGateway

    override fun onAttach(context: Context) {
        injectComponent()
        super.onAttach(context)
    }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(provideLayoutId(), container, false)
        onViewBound(view, savedInstanceState)
        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    fun getSlidingPanel(): SlidingUpPanelLayout? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    protected open fun injectComponent(){

    }

}