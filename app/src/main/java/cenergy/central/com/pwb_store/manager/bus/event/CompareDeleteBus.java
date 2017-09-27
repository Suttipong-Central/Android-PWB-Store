package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductList;

/**
 * Created by napabhat on 9/19/2017 AD.
 */

public class CompareDeleteBus {

    private ProductList mProductList;
    private boolean isCancel;

    public CompareDeleteBus(ProductList productList, boolean isCancel){
        this.mProductList = productList;
        this.isCancel = isCancel;
    }

    public ProductList getProductList() {
        return mProductList;
    }

    public boolean isCancel() {
        return isCancel;
    }
}
