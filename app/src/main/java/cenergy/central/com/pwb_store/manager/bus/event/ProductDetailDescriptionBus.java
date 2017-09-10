package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.PromotionDetailText;

/**
 * Created by napabhat on 10/26/2016 AD.
 */

public class ProductDetailDescriptionBus {
    private View view;
    private PromotionDetailText mPromotionDetailText;

    public ProductDetailDescriptionBus(View view, PromotionDetailText promotionDetailText) {
        this.view = view;
        this.mPromotionDetailText = promotionDetailText;
    }

    public View getView() {
        return view;
    }
    public PromotionDetailText getPromotionDetailText() {
        return mPromotionDetailText;
    }
}
