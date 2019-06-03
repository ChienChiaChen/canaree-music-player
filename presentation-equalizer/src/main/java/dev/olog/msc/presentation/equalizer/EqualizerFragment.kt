package dev.olog.msc.presentation.equalizer

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dev.olog.msc.core.equalizer.IEqualizer
import dev.olog.msc.presentation.base.bottom.sheet.BaseBottomSheetFragment
import dev.olog.msc.presentation.base.extensions.viewModelProvider
import dev.olog.msc.presentation.equalizer.widget.RadialKnob
import dev.olog.msc.shared.ui.extensions.subscribe
import kotlinx.android.synthetic.main.fragment_equalizer.*
import kotlinx.android.synthetic.main.fragment_equalizer.view.*
import javax.inject.Inject

class EqualizerFragment : BaseBottomSheetFragment(), IEqualizer.Listener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy { viewModelProvider<EqualizerFragmentViewModel>(factory) }
    private lateinit var adapter: PresetPagerAdapter
    private var snackBar: Snackbar? = null



    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val presets = viewModel.getPresets()
        adapter = PresetPagerAdapter(childFragmentManager, presets.toMutableList())

        if (presets.isNotEmpty()) {
            view.pager.adapter = adapter
            view.pager.currentItem = viewModel.getCurrentPreset()
            view.pageIndicator.setViewPager(view.pager)
        }

        view.powerSwitch.isChecked = viewModel.isEqualizerEnabled()

        view.bassKnob.setMax(100)
        view.virtualizerKnob.setMax(100)
        view.bassKnob.setValue(viewModel.getBassStrength())
        view.virtualizerKnob.setValue(viewModel.getVirtualizerStrength())

        view.band1.initializeBandHeight(viewModel.getBandLevel(0))
        view.band2.initializeBandHeight(viewModel.getBandLevel(1))
        view.band3.initializeBandHeight(viewModel.getBandLevel(2))
        view.band4.initializeBandHeight(viewModel.getBandLevel(3))
        view.band5.initializeBandHeight(viewModel.getBandLevel(4))

        viewModel.isEqualizerAvailable()
            .subscribe(viewLifecycleOwner) { isEqAvailable ->
                if (snackBar != null) {
                    if (isEqAvailable) {
                        snackBar?.dismiss()
                    } // else, already shown
                } else {
                    // error snackBar now shown
                    if (!isEqAvailable) {
                        snackBar = Snackbar.make(root, R.string.equalizer_error, Snackbar.LENGTH_INDEFINITE)
                        snackBar!!.show()
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        bassKnob.setOnKnobChangeListener(onBassKnobChangeListener)
        virtualizerKnob.setOnKnobChangeListener(onVirtualizerKnobChangeListener)
        pager.addOnPageChangeListener(onPageChangeListener)
        viewModel.addEqualizerListener(this)

        band1.setLevel = onBandLevelChange
        band2.setLevel = onBandLevelChange
        band3.setLevel = onBandLevelChange
        band4.setLevel = onBandLevelChange
        band5.setLevel = onBandLevelChange
        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) R.string.common_switch_on else R.string.common_switch_off
            powerSwitch.text = getString(text)
            viewModel.setEqualizerEnabled(isChecked)
        }
    }

    override fun onPause() {
        super.onPause()
        bassKnob.setOnKnobChangeListener(null)
        virtualizerKnob.setOnKnobChangeListener(null)
        pager.removeOnPageChangeListener(onPageChangeListener)
        viewModel.removeEqualizerListener(this)
        powerSwitch.setOnCheckedChangeListener(null)

        band1.setLevel = null
        band2.setLevel = null
        band3.setLevel = null
        band4.setLevel = null
        band5.setLevel = null
    }

    private val onBassKnobChangeListener = object : RadialKnob.OnKnobChangeListener {
        override fun onValueChanged(knob: RadialKnob?, value: Int, fromUser: Boolean) {
            viewModel.setBassStrength(value)
        }

        override fun onSwitchChanged(knob: RadialKnob?, on: Boolean): Boolean = false
    }

    private val onVirtualizerKnobChangeListener = object : RadialKnob.OnKnobChangeListener {
        override fun onValueChanged(knob: RadialKnob?, value: Int, fromUser: Boolean) {
            viewModel.setVirtualizerStrength(value)
        }

        override fun onSwitchChanged(knob: RadialKnob?, on: Boolean): Boolean = false
    }

    private val onPageChangeListener = object : androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            viewModel.setPreset(position % adapter.count)
        }
    }

    private val onBandLevelChange = { band: Int, level: Float ->
        viewModel.setBandLevel(band, level)
    }

    override fun onPresetChange(band: Int, level: Float) {
        band1.onPresetChange(band, level)
        band2.onPresetChange(band, level)
        band3.onPresetChange(band, level)
        band4.onPresetChange(band, level)
        band5.onPresetChange(band, level)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}