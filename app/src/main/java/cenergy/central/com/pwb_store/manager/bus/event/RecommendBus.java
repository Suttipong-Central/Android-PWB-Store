package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.ProductRelatedList;

/**
 * Created by napabhat on 9/25/2017 AD.
 */

public class RecommendBus {
    private View view;
    private ProductRelatedList mRelatedList;

    public RecommendBus(View view, ProductRelatedList productRelatedList){
        this.view = view;
        this.mRelatedList = productRelatedList;
    }

    public ProductRelatedList getRelatedList() {
        return mRelatedList;
    }

    public View getView() {
        return view;
    }
}
