package cenergy.central.com.pwb_store.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.util.Log;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.TypeFaceManager;

/**
 * Created by napabhat on 7/11/2017 AD.
 */

public class PowerBuyEditText extends TextInputEditText {
    public PowerBuyEditText(Context context) {
        super(context);

        if (isInEditMode())
            return;

        initInstance(context, null);
    }

    public PowerBuyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode())
            return;

        initInstance(context, attrs);
    }

    public PowerBuyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode())
            return;

        initInstance(context, attrs);
    }

    private void initInstance(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PowerBuyEditText);

            if (ta != null) {
                String fontAsset = ta.getString(R.styleable.PowerBuyEditText_typeFaceAsset);
                if (fontAsset != null) {
                    fontAsset = "fonts/" + fontAsset;
                    Typeface tf = TypeFaceManager.getInstance().getFont(fontAsset);
                    int style = Typeface.NORMAL;
                    float size = getTextSize();

                    if (getTypeface() != null)
                        style = getTypeface().getStyle();

                    if (tf != null)
                        setTypeface(tf, style);
                    else
                        Log.d("FontText", String.format("Could not create a font from asset: %s", fontAsset));
                }

                ta.recycle();
            }
        }
    }
}

