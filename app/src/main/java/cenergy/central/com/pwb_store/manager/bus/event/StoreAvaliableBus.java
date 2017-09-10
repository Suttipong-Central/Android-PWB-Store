package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class StoreAvaliableBus {
    private View view;
    private String productId;

    public StoreAvaliableBus(View view, String productId) {
        this.view = view;
        this.productId = productId;
    }

    public View getView() {
        return view;
    }

    public String getProductId() {
        return productId;
    }
}
