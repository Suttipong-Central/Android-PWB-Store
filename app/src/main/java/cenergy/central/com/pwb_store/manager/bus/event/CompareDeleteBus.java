package cenergy.central.com.pwb_store.manager.bus.event;

import cenergy.central.com.pwb_store.model.ProductCompareList;

/**
 * Created by napabhat on 9/19/2017 AD.
 */

public class CompareDeleteBus {

    private ProductCompareList mProductCompareList;
    private boolean isCancel;

    public CompareDeleteBus(ProductCompareList productList, boolean isCancel){
        this.mProductCompareList = productList;
        this.isCancel = isCancel;
    }

    public ProductCompareList getProductCompareList() {
        return mProductCompareList;
    }

    public boolean isCancel() {
        return isCancel;
    }
}
