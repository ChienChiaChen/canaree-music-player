package dev.olog.msc.presentation.base.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import dev.olog.msc.presentation.base.R;

public class DottedSeparator extends View {

    public DottedSeparator(Context context) {
        this(context, null);
    }

    public DottedSeparator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DottedSeparator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundResource(R.drawable.dotted_line);
        int color = ContextCompat.getColor(context, R.color.dotted_line);
        setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
