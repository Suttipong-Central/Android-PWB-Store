package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 7/20/2017 AD.
 */

public class ProductBackBus {

    private boolean isHome;

    public ProductBackBus(boolean isHome){
        this.isHome = isHome;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }
}
