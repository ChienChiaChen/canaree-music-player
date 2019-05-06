package dev.olog.presentation.base.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import dev.olog.msc.shared.ui.R;
import dev.olog.msc.shared.ui.theme.AppTheme;

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
        if (AppTheme.INSTANCE.isDarkTheme()){
            setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            setAlpha(.2f);
        } else {
            setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            setAlpha(.1f);
        }
    }
}
