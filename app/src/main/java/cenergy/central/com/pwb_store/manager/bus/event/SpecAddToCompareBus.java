package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 11/11/2017 AD.
 */

public class SpecAddToCompareBus {
    private View mView;
    private boolean isAdded;

    public SpecAddToCompareBus(View view, boolean isAdded){
        this.isAdded = isAdded;
        this.mView = view;
    }

    public View getView() {
        return mView;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }
}
