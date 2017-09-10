package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.PromotionItem;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionItemBus {
    private PromotionItem mPromotionItem;
    private View mView;

    public PromotionItemBus(PromotionItem promotionItem, View view){
        this.mPromotionItem = promotionItem;
        this. mView = view;
    }

    public PromotionItem getPromotionItem() {
        return mPromotionItem;
    }

    public View getView() {
        return mView;
    }
}
