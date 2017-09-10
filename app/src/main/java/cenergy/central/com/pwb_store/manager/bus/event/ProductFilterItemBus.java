package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductFilterItem;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterItemBus {
    private ProductFilterItem productFilterItem;
    private int position;

    public ProductFilterItemBus(ProductFilterItem productFilterItem, int position) {
        this.productFilterItem = productFilterItem;
        this.position = position;
    }

    public ProductFilterItem getProductFilterItem() {
        return productFilterItem;
    }

    public int getPosition() {
        return position;
    }
}
