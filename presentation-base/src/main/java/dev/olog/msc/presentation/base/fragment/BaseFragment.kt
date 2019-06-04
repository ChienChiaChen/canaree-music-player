package dev.olog.msc.presentation.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.olog.msc.presentation.base.R
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.base.interfaces.HasSlidingPanel
import dev.olog.msc.presentation.base.interfaces.SuperCerealBottomSheetBehavior
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.extensions.dip
import dev.olog.msc.shared.ui.extensions.setMargin

abstract class BaseFragment : Fragment() {

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

    fun getSlidingPanel(): SuperCerealBottomSheetBehavior<*>? {
        return (activity as HasSlidingPanel).getSlidingPanel()
    }

    protected open fun injectComponent(){

    }

    protected fun setupFabInset(fab: FloatingActionButton){
        fab.setMargin(bottomPx = fab.marginBottom + ctx.dimen(R.dimen.sliding_panel_peek) + ctx.dimen(R.dimen.bottom_navigation_height))
    }

    protected fun setupListInset(list: RecyclerView){
        val topInset = ctx.dimen(R.dimen.tab) + ctx.dip(8)
        val bottomInset =  ctx.dimen(R.dimen.sliding_panel_peek) + ctx.dip(8)
        list.updatePadding(top = topInset, bottom = bottomInset)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> getArgument(key: String): T {
        return arguments!!.get(key) as T
    }

}