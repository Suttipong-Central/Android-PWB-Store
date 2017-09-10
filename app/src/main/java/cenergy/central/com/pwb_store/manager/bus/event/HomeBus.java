package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 7/17/2017 AD.
 */

public class HomeBus {
    private boolean isHome;

    public HomeBus(boolean isHome){
        this.isHome = isHome;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }
}
