package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 8/25/2017 AD.
 */

public class BookTimeBus {
    private View view;

    public BookTimeBus(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }
}
