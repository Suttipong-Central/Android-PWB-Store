package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.SortingItem;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingItemBus {
    private SortingItem sortingItem;
    private int position;

    public SortingItemBus(SortingItem sortingItem, int position) {
        this.sortingItem = sortingItem;
        this.position = position;
    }


    public SortingItem getSortingItem() {
        return sortingItem;
    }

    public int getPosition() {
        return position;
    }
}
