package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by C.Thawanapong on 9/9/2016 AD.
 * Email chavit.t@th.zalora.com
 */

public class WebViewEndedBus {
    private boolean isWebViewEnded;

    public WebViewEndedBus(boolean isWebViewEnded) {
        this.isWebViewEnded = isWebViewEnded;
    }

    public boolean isWebViewEnded() {
        return isWebViewEnded;
    }

    public void setWebViewEnded(boolean webViewEnded) {
        isWebViewEnded = webViewEnded;
    }
}
