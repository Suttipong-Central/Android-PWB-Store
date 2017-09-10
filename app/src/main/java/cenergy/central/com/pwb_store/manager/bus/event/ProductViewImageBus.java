package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.ProductDetailImage;

/**
 * Created by napabhat on 10/31/2016 AD.
 */

public class ProductViewImageBus {
    private ProductDetailImage mProductDetailImage;
    private View view;

    public ProductViewImageBus(View view, ProductDetailImage productDetailImage) {
        this.view = view;
        this.mProductDetailImage = productDetailImage;
    }

    public ProductDetailImage getProductDetailImage() {
        return mProductDetailImage;
    }

    public View getView() {
        return view;
    }
}
