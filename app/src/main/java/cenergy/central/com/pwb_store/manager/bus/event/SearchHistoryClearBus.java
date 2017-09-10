package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchHistoryClearBus {
    private boolean isClicked;

    public SearchHistoryClearBus(boolean isClicked) {
        this.isClicked = isClicked;
    }

    public boolean isClicked() {
        return isClicked;
    }
}
