package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 8/10/2017 AD.
 */

public class SearchEventBus {

    private View view;
    private boolean isClick;

    public SearchEventBus(View view, boolean isClick) {
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
