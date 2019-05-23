package dev.olog.msc.presentation.about

import android.content.res.ColorStateList
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.SimpleAdapter
import dev.olog.msc.presentation.base.model.DisplayableItem
import dev.olog.msc.presentation.navigator.NavigatorAbout
import dev.olog.msc.shared.ui.extensions.colorPrimary
import kotlinx.android.synthetic.main.item_about.view.*


class AboutActivityAdapter(
    private val navigator: NavigatorAbout,
    private val presenter: AboutActivityPresenter

) : SimpleAdapter<DisplayableItem>() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            val item = data[viewHolder.adapterPosition]
            val activity = viewHolder.itemView.context as FragmentActivity
            when (item.mediaId) {
                AboutActivityPresenter.THIRD_SW_ID -> navigator.toLicensesFragment(activity)
                AboutActivityPresenter.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment(activity)
                AboutActivityPresenter.RATE_ID -> navigator.toMarket(activity)
                AboutActivityPresenter.PRIVACY_POLICY -> navigator.toPrivacyPolicy(activity)
                AboutActivityPresenter.BUY_PRO -> presenter.buyPro()
//                AboutActivityPresenter.COMMUNITY -> navigator.joinCommunity(activity)
                AboutActivityPresenter.BETA -> navigator.joinBeta(activity)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        if (item.mediaId == AboutActivityPresenter.BUY_PRO) {
            val view = binding.root
            view.title.setTextColor(ColorStateList.valueOf(view.context.colorPrimary()))
        }
        binding.setVariable(BR.item, item)
    }

}