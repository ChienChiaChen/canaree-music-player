package dev.olog.msc.presentation.about

import android.content.res.ColorStateList
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.presentation.navigator.NavigatorAbout
import dev.olog.msc.shared.ui.extensions.colorAccent
import dev.olog.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.extensions.setOnClickListener
import dev.olog.presentation.base.interfaces.MediaProvider
import dev.olog.presentation.base.model.DisplayableItem
import kotlinx.android.synthetic.main.item_about.view.*


class AboutActivityAdapter (
        lifecycle: Lifecycle,
        private val navigator: NavigatorAbout,
        private val presenter: AboutActivityPresenter

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            val activity = viewHolder.itemView.context as FragmentActivity
            when (item.mediaId){
                AboutActivityPresenter.THIRD_SW_ID -> navigator.toLicensesFragment(activity)
                AboutActivityPresenter.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment(activity)
                AboutActivityPresenter.RATE_ID -> navigator.toMarket(activity)
                AboutActivityPresenter.PRIVACY_POLICY -> navigator.toPrivacyPolicy(activity)
                AboutActivityPresenter.BUY_PRO -> presenter.buyPro()
                AboutActivityPresenter.COMMUNITY -> navigator.joinCommunity(activity)
                AboutActivityPresenter.BETA -> navigator.joinBeta(activity)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        if (item.mediaId == AboutActivityPresenter.BUY_PRO){
            val view = binding.root
            view.title.setTextColor(ColorStateList.valueOf(view.context.colorAccent()))
        }
        binding.setVariable(BR.item, item)
    }

}