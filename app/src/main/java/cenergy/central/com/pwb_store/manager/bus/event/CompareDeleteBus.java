package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.CompareProduct;
import cenergy.central.com.pwb_store.model.ProductCompareList;

/**
 * Created by napabhat on 9/19/2017 AD.
 */

public class CompareDeleteBus {

    private ProductCompareList mProductCompareList;
    private CompareProduct compareProduct;
    private boolean isCancel;

    public CompareDeleteBus(ProductCompareList productList, boolean isCancel){
        this.mProductCompareList = productList;
        this.isCancel = isCancel;
    }

    public CompareDeleteBus(CompareProduct compareProduct, boolean isCancel) {
        this.compareProduct = compareProduct;
        this.isCancel = isCancel;
    }

    public CompareDeleteBus(CompareProduct compareProduct) {
        this. compareProduct = compareProduct;
    }

    public CompareProduct getCompareProduct() {
        return compareProduct;
    }

    public ProductCompareList getProductCompareList() {
        return mProductCompareList;
    }

    public boolean isCancel() {
        return isCancel;
    }
}
