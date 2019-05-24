package dev.olog.msc.presentation.tabs.foldertree

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.base.fragment.BaseFragment
import dev.olog.msc.presentation.base.interfaces.CanHandleOnBackPressed
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.tabs.foldertree.widgets.BreadCrumbLayout
import dev.olog.msc.shared.extensions.lazyFast
import dev.olog.msc.presentation.tabs.foldertree.utils.safeGetCanonicalFile
import dev.olog.msc.shared.ui.extensions.subscribe
import dev.olog.msc.shared.ui.extensions.toggleVisibility
import kotlinx.android.synthetic.main.fragment_folder_tree.*
import kotlinx.android.synthetic.main.fragment_folder_tree.view.*
import javax.inject.Inject

class FolderTreeFragment : BaseFragment(), BreadCrumbLayout.SelectionCallback,
    CanHandleOnBackPressed {

    companion object {

        @JvmStatic
        fun newInstance(): FolderTreeFragment {
            return FolderTreeFragment()
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var navigator: Navigator
    private val viewModel by lazyFast { viewModelProvider<FolderTreeFragmentViewModel>(viewModelFactory) }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val adapter = FolderTreeFragmentAdapter(lifecycle, viewModel, navigator)
        view.list.adapter = adapter
        view.list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        view.list.setHasFixedSize(true)

        view.fastScroller.attachRecyclerView(view.list)
        view.fastScroller.showBubble(false)


//            view.bread_crumbs.setBackgroundColor(ctx.colorSurface()) TODO check color on dark mode

        viewModel.observeFile()
                .subscribe(viewLifecycleOwner) {
                    bread_crumbs.setActiveOrAdd(BreadCrumbLayout.Crumb(it), false)
                }

        viewModel.observeChildren()
                .subscribe(viewLifecycleOwner, adapter::updateDataSet)

        viewModel.observeDefaultFolder()
                .subscribe(viewLifecycleOwner) { isInDefaultFolder ->
                    defaultFolder.toggleVisibility(!isInDefaultFolder, true)
                }
    }

    override fun onResume() {
        super.onResume()
        bread_crumbs.setCallback(this)
        defaultFolder.setOnClickListener { viewModel.updateDefaultFolder() }
    }

    override fun onPause() {
        super.onPause()
        bread_crumbs.setCallback(null)
        defaultFolder.setOnClickListener(null)
    }

    override fun onCrumbSelection(crumb: BreadCrumbLayout.Crumb, index: Int) {
        viewModel.nextFolder(crumb.file.safeGetCanonicalFile())
    }

    override fun handleOnBackPressed(): Boolean {
        return viewModel.popFolder()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_folder_tree
}