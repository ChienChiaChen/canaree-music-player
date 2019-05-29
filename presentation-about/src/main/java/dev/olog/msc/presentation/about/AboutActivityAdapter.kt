package dev.olog.msc.presentation.about

import android.content.res.ColorStateList
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import dev.olog.msc.app.injection.navigator.NavigatorAbout
import dev.olog.msc.presentation.base.list.DataBoundViewHolder
import dev.olog.msc.presentation.base.list.ObservableAdapter
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.shared.ui.extensions.colorPrimary
import kotlinx.android.synthetic.main.item_about.view.*


class AboutActivityAdapter(
    private val lifecycle: Lifecycle,
    private val navigator: NavigatorAbout,
    private val presenter: AboutActivityViewModel

) : ObservableAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            val item = data[viewHolder.adapterPosition]
            val activity = viewHolder.itemView.context as FragmentActivity
            when (item.mediaId) {
                AboutActivityViewModel.THIRD_SW_ID -> navigator.toLicensesFragment(activity)
                AboutActivityViewModel.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment(activity)
                AboutActivityViewModel.RATE_ID -> navigator.toMarket(activity)
                AboutActivityViewModel.PRIVACY_POLICY -> navigator.toPrivacyPolicy(activity)
                AboutActivityViewModel.BUY_PRO -> presenter.buyPro()
//                AboutActivityPresenter.COMMUNITY -> navigator.joinCommunity(activity)
                AboutActivityViewModel.BETA -> navigator.joinBeta(activity)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        if (item.mediaId == AboutActivityViewModel.BUY_PRO) {
            val view = binding.root
            view.title.setTextColor(ColorStateList.valueOf(view.context.colorPrimary()))
        }
        binding.setVariable(BR.item, item)
    }

}