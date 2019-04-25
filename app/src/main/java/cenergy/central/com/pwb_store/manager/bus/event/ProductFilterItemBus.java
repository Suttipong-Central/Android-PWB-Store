package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.Category;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterItemBus {
    private Category category;
    private int position;

    public ProductFilterItemBus(Category category, int position) {
        this.category = category;
        this.position = position;
    }

    public Category getProductFilterItem() {
        return category;
    }

    public int getPosition() {
        return position;
    }
}
