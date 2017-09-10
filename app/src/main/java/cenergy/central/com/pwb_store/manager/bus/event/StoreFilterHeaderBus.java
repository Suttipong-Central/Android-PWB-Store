package cenergy.central.com.pwb_store.manager.bus.event;
import cenergy.central.com.pwb_store.model.StoreFilterHeader;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class StoreFilterHeaderBus {
    private StoreFilterHeader mStoreFilterHeader;
    private int position;

    public StoreFilterHeaderBus(StoreFilterHeader storeFilterHeader, int position) {
        this.mStoreFilterHeader = storeFilterHeader;
        this.position = position;
    }

    public StoreFilterHeader getStoreFilterHeader() {
        return mStoreFilterHeader;
    }

    public int getPosition() {
        return position;
    }
}
