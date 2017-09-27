package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.StoreList;

/**
 * Created by napabhat on 9/25/2017 AD.
 */

public class StoreListBus {
    private StoreList mStoreList;

    public StoreListBus(StoreList storeList){
        this.mStoreList = storeList;
    }

    public StoreList getStoreList() {
        return mStoreList;
    }
}
