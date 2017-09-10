package cenergy.central.com.pwb_store.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Strings;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.TypeFaceManager;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

@SuppressLint("AppCompatCustomView")
public class PowerBuyTextView extends TextView {
    private boolean enableStrikeThrough = false;

    public PowerBuyTextView(Context context) {
        super(context);

        if (isInEditMode())
            return;

        initInstance(context, null);
    }

    public PowerBuyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode())
            return;

        initInstance(context, attrs);
    }

    public PowerBuyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode())
            return;

        initInstance(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PowerBuyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (isInEditMode())
            return;

        initInstance(context, attrs);
    }

    private void initInstance(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PowerBuyTextView);

            if (ta != null) {
                String fontAsset = ta.getString(R.styleable.PowerBuyTextView_typeFaceAsset);
                if (!Strings.isNullOrEmpty(fontAsset)) {
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

    public void setEnableStrikeThrough(boolean enableStrikeThrough) {
        this.enableStrikeThrough = enableStrikeThrough;

        if (enableStrikeThrough) {
            setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            setPaintFlags(getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}

