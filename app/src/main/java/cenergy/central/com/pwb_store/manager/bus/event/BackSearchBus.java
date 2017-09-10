package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 7/11/2017 AD.
 */

public class BackSearchBus {
    private View view;
    private boolean isClick;

    public BackSearchBus(View view, boolean isClick) {
        this.isClick = isClick;
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public boolean isClick() {
        return isClick;
    }
}
