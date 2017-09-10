package cenergy.central.com.pwb_store.manager.bus.event;


import cenergy.central.com.pwb_store.model.ProductDetailOptionItem;

/**
 * Created by napabhat on 2/1/2017 AD.
 */

public class ProductDetailOptionItemBus {

    private ProductDetailOptionItem mProductDetailOptionItem;
    private int adapterPosition;

    public ProductDetailOptionItemBus(ProductDetailOptionItem productDetailOptionItem, int adapterPosition) {
        this.mProductDetailOptionItem = productDetailOptionItem;
        this.adapterPosition = adapterPosition;
    }

    public ProductDetailOptionItem getProductDetailOptionItem() {
        return mProductDetailOptionItem;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }
}
