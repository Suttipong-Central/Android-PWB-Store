package cenergy.central.com.pwb_store.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import cenergy.central.com.pwb_store.manager.TypeFaceManager;


/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PowerBuyTabLayout extends TabLayout {
    public PowerBuyTabLayout(Context context) {
        super(context);
    }

    public PowerBuyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerBuyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setupWithViewPager(ViewPager viewPager) {
        super.setupWithViewPager(viewPager);


        String fontAsset = "fonts/RobotoLight.ttf";
        Typeface mTypeface = TypeFaceManager.getInstance().getFont(fontAsset);

        if (mTypeface != null) {
            this.removeAllTabs();

            ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);

            PagerAdapter adapter = viewPager.getAdapter();

            for (int i = 0, count = adapter.getCount(); i < count; i++) {
                Tab tab = this.newTab();
                this.addTab(tab.setText(adapter.getPageTitle(i)));
                AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
                view.setTypeface(mTypeface, Typeface.NORMAL);
            }
        } else {
            Log.d("FontText", String.format("Could not create a font from asset: %s", fontAsset));
        }

    }
}