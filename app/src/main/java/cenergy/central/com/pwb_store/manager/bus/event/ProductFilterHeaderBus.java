package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductFilterHeader;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterHeaderBus {
    private ProductFilterHeader mProductFilterHeader;
    private int position;

    public ProductFilterHeaderBus(ProductFilterHeader mProductFilterHeader, int position) {
        this.mProductFilterHeader = mProductFilterHeader;
        this.position = position;
    }

    public ProductFilterHeader getProductFilterHeader() {
        return mProductFilterHeader;
    }

    public int getPosition() {
        return position;
    }
}
