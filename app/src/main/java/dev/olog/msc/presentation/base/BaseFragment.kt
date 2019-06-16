package dev.olog.msc.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import dagger.android.support.DaggerFragment
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior

abstract class BaseFragment : DaggerFragment(), HasSlidingPanel {

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(provideLayoutId(), container, false)
        onViewBound(view, savedInstanceState)
        return view
    }

    protected open fun onViewBound(view: View, savedInstanceState: Bundle?) {}

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    override fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*> {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

}