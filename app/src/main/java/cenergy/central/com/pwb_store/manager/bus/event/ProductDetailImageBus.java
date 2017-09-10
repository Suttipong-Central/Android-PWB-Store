package cenergy.central.com.pwb_store.manager.bus.event;

import android.view.View;

import cenergy.central.com.pwb_store.model.ProductDetailImage;
import cenergy.central.com.pwb_store.model.ProductDetailImageItem;


/**
 * Created by napabhat on 2/9/2017 AD.
 */

public class ProductDetailImageBus {
    private View mView;
    private ProductDetailImageItem mProductDetailImageItem;
    private ProductDetailImage mProductDetailImage;
    private boolean isClicked;

    public ProductDetailImageBus(View view, ProductDetailImageItem productDetailImageItem, boolean isClicked, ProductDetailImage productDetailImage) {
        this.mView = view;
        this.mProductDetailImageItem = productDetailImageItem;
        this.mProductDetailImage = productDetailImage;
        this.isClicked = isClicked;
    }

    public View getView() {
        return mView;
    }

    public ProductDetailImageItem getProductDetailImageItem() {
        return mProductDetailImageItem;
    }

    public ProductDetailImage getProductDetailImage() {
        return mProductDetailImage;
    }

    public boolean isClicked() {
        return isClicked;
    }
}
