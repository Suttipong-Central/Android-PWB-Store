package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 7/19/2017 AD.
 */

public class ProductDetailBus {
    private View view;
    private String productId;

    public ProductDetailBus(String productId, View view) {
        this.productId = productId;
        this.view = view;
    }

    public String getProductId() {
        return productId;
    }

    public View getView() {
        return view;
    }
}
