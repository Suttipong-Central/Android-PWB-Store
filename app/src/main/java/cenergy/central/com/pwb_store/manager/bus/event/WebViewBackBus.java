package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by C.Thawanapong on 9/9/2016 AD.
 * Email chavit.t@th.zalora.com
 */

public class WebViewBackBus {
    private boolean isBackPressed;

    public WebViewBackBus(boolean isBackPressed) {
        this.isBackPressed = isBackPressed;
    }

    public boolean isBackPressed() {
        return isBackPressed;
    }

    public void setBackPressed(boolean backPressed) {
        isBackPressed = backPressed;
    }
}
