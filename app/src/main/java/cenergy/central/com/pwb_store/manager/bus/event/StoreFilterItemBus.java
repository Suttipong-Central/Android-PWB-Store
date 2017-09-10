package cenergy.central.com.pwb_store.manager.bus.event;
import cenergy.central.com.pwb_store.model.StoreFilterItem;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class StoreFilterItemBus {
    private StoreFilterItem mStoreFilterItem;
    private int position;

    public StoreFilterItemBus(StoreFilterItem storeFilterItem, int position) {
        this.mStoreFilterItem = storeFilterItem;
        this.position = position;
    }

    public StoreFilterItem getStoreFilterItem() {
        return mStoreFilterItem;
    }

    public int getPosition() {
        return position;
    }
}
