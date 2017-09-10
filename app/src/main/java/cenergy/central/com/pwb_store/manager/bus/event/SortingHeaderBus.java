package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.SortingHeader;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingHeaderBus {
    private SortingHeader mSortingHeader;
    private int position;

    public SortingHeaderBus(SortingHeader sortingHeader, int position) {
        this.mSortingHeader = sortingHeader;
        this.position = position;
    }

    public SortingHeader getSortingHeader() {
        return mSortingHeader;
    }

    public int getPosition() {
        return position;
    }
}
