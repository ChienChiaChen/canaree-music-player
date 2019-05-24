package dev.olog.msc.presentation.detail.listener

import android.view.View
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.R
import dev.olog.msc.shared.core.lazyFast
import dev.olog.msc.shared.extensions.dimen
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import kotlinx.android.synthetic.main.fragment_detail.view.*

class HeaderVisibilityScrollListener(
        private val fragment: DetailFragment

) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    private val toolbarHeight by lazyFast {
        val statusBarHeight = fragment.view!!.statusBar.height
        statusBarHeight + fragment.ctx.dimen(R.dimen.toolbar)
    }

    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        val child = recyclerView.getChildAt(0)
        val holder = recyclerView.getChildViewHolder(child)

        val view = fragment.view!!

        if (holder.itemViewType == R.layout.item_detail_item_image) {
            val bottom = child.bottom - child.findViewById<View>(R.id.textWrapper).height
            val needDarkLayout = bottom - toolbarHeight < 0

            view.statusBar.toggleVisibility(needDarkLayout, false)
            view.toolbar.toggleVisibility(needDarkLayout, false)
            view.headerText.toggleVisibility(needDarkLayout, false)

            fragment.hasLightStatusBarColor = needDarkLayout

        } else {
            view.statusBar.toggleVisibility(true, false)
            view.toolbar.toggleVisibility(true, false)
            view.headerText.toggleVisibility(true, false)

            fragment.hasLightStatusBarColor = true
        }
    }

}