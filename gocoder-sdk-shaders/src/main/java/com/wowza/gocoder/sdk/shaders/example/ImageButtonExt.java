package com.wowza.gocoder.sdk.shaders.example;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ImageButtonExt extends ImageButton {

    public ImageButtonExt(Context context) {
        super(context);
    }

    public ImageButtonExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageButtonExt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        Drawable background = getBackground();
        Drawable drawable   = getDrawable();

        if (enabled) {
            if (background != null)
                background.setColorFilter(null);
            if (drawable != null) {
                drawable.setColorFilter(null);
            }
        }
        else {
            if (background != null)
                background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            if (drawable != null)
                drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        setImageAlpha(enabled ? 255 : 122);
        setAlpha(enabled ? 1f : 0.5f);
    }
}
