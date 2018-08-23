package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.Product;
import cenergy.central.com.pwb_store.model.ProductDetail;

/**
 * Created by napabhat on 9/14/2017 AD.
 */

public class ProductBus {

    private ProductDetail mProductDetail;
    private Product product; // new model
    private String action; // action

    // constants
    public static final String ACTION_ADD_TO_CART = "action_add_to_cart";
    public static final String ACTION_ADD_TO_COMPARE = "action_add_to_compare";

    public ProductBus(ProductDetail productDetail) {
        this.mProductDetail = productDetail;
    }

    public ProductBus(Product product, String action) {
        this.product = product;
        this.action = action;
    }

    public ProductDetail getProductDetail() {
        return mProductDetail;
    }

    public Product getProduct() {
        return product;
    }

    public String getAction() {
        return action;
    }
}
