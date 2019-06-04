package dev.olog.msc.presentation.sleeptimer

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import dev.olog.msc.presentation.base.extensions.ctx
import dev.olog.msc.shared.ui.extensions.colorPrimary

open class ScrollHmsPickerDialog : DialogFragment() {
    interface HmsPickHandler {
        fun onHmsPick(reference: Int, hours: Int, minutes: Int, seconds: Int)
    }

    private var autoStep: Boolean = false

    var dismissListener: DialogInterface.OnDismissListener? = null
    var pickListener: HmsPickHandler? = null

    protected lateinit var hmsPicker: ScrollHmsPicker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.picker_fragment, container, false)
        hmsPicker = view.findViewById<ScrollHmsPicker>(R.id.hms_picker).also { picker ->
            picker.setAutoStep(autoStep)
            picker.setColorNormal(ContextCompat.getColor(ctx, android.R.color.darker_gray))
            picker.setColorSelected(ctx.colorPrimary())
        }
//        if (context.isDark()) { TODO color from res
//            val background = ContextCompat.getColor(view.context, R.color.theme_dark_background)
//            (view as MaterialCardView).backgroundTintList = ColorStateList.valueOf(background)
//            hmsPicker.backgroundTintList = ColorStateList.valueOf(background)
//        }
        val textColor = ctx.colorPrimary()
        view.findViewById<Button>(R.id.button_cancel).apply {
            setTextColor(textColor)
            setOnClickListener { dismiss() }
        }
        view.findViewById<Button>(R.id.button_ok).apply {
            setTextColor(textColor)
        }
        dialog?.run {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

}