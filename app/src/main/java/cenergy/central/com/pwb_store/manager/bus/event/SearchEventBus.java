package cenergy.central.com.pwb_store.manager.bus.event;

/**
 * Created by napabhat on 8/10/2017 AD.
 */

public class SearchEventBus {

    //private View view;
    private boolean isClick;
    private String keyword;

    public SearchEventBus(boolean isClick, String keyword) {
        this.isClick = isClick;
        //this.view = view;
        this.keyword = keyword;
    }

//    public View getView() {
//        return view;
//    }

    public boolean isClick() {
        return isClick;
    }

    public String getKeyword() {
        return keyword;
    }
}
