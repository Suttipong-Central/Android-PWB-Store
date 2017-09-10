package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by C.Thawanapong on 9/9/2016 AD.
 * Email chavit.t@th.zalora.com
 */

public class WebViewTitleBus {
    private String webTitle;
    private String webUrl;

    public WebViewTitleBus(String webTitle, String webUrl) {
        this.webTitle = webTitle;
        this.webUrl = webUrl;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
