package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchQueryBus {
    private View view;
    private String searchQuery;
    private String id;

    public SearchQueryBus(View view, String searchQuery, String id) {
        this.view = view;
        this.searchQuery = searchQuery;
        this.id = id;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public View getView() {
        return view;
    }

    public String getId() {
        return id;
    }
}
