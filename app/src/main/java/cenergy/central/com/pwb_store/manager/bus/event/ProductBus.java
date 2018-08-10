package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetail;

/**
 * Created by napabhat on 9/14/2017 AD.
 */

public class ProductBus {

    private ProductDetail mProductDetail;
    private Product product; // new model

    public ProductBus(ProductDetail productDetail){
        this.mProductDetail = productDetail;
    }

    public ProductBus(Product product) {
        this.product = product;
    }

    public ProductDetail getProductDetail() {
        return mProductDetail;
    }

    public Product getProduct() {
        return product;
    }
}
