package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterSubHeaderBus {
    private ProductFilterSubHeader mProductFilterSubHeader;
    private int position;

    public ProductFilterSubHeaderBus(ProductFilterSubHeader mProductFilterSubHeader, int position) {
        this.mProductFilterSubHeader = mProductFilterSubHeader;
        this.position = position;
    }

    public ProductFilterSubHeader getProductFilterSubHeader() {
        return mProductFilterSubHeader;
    }

    public int getPosition() {
        return position;
    }
}
