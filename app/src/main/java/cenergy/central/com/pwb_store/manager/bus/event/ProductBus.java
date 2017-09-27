package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductDetail;

/**
 * Created by napabhat on 9/14/2017 AD.
 */

public class ProductBus {

    private ProductDetail mProductDetail;

    public ProductBus(ProductDetail productDetail){
        this.mProductDetail = productDetail;
    }

    public ProductDetail getProductDetail() {
        return mProductDetail;
    }
}
