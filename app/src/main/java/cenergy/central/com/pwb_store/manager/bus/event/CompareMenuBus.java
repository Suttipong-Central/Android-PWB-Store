package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 9/28/2017 AD.
 */

public class CompareMenuBus {
    private View mView;

    public CompareMenuBus(View view){
        this.mView = view;
    }

    public View getView() {
        return mView;
    }
}
