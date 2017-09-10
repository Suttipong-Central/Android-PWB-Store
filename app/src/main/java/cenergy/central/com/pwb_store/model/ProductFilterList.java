package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by napabhat on 7/14/2017 AD.
 */

public class ProductFilterList  implements IViewType, Parcelable {

    public static final Creator<ProductFilterList> CREATOR = new Creator<ProductFilterList>() {
        @Override
        public ProductFilterList createFromParcel(Parcel in) {
            return new ProductFilterList(in);
        }

        @Override
        public ProductFilterList[] newArray(int size) {
            return new ProductFilterList[size];
        }
    };
    private int viewTypeId;
    private int total;
    private List<ProductFilterHeader> mProductFilterHeaders;

    public ProductFilterList(List<ProductFilterHeader> productFilterHeaders) {
        this.mProductFilterHeaders = productFilterHeaders;
    }

    public ProductFilterList(ProductFilterList productFilterList) {
        this.total = productFilterList.getTotal();
        List<ProductFilterHeader> productFilterHeaderList = new ArrayList<>();

        if (productFilterList.isProductFilterListAvailable()) {
            for (ProductFilterHeader productFilterHeader :
                    productFilterList.getProductFilterHeaders()) {
                productFilterHeaderList.add(new ProductFilterHeader(productFilterHeader));
            }
        }

        this.mProductFilterHeaders = productFilterHeaderList;
    }

    protected ProductFilterList(Parcel in) {
        viewTypeId = in.readInt();
        mProductFilterHeaders = in.createTypedArrayList(ProductFilterHeader.CREATOR);
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(mProductFilterHeaders);
        dest.writeInt(total);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<ProductFilterHeader> getProductFilterHeaders() {
        return mProductFilterHeaders;
    }

    public void setProductFilterHeaders(List<ProductFilterHeader> productFilterHeaders) {
        mProductFilterHeaders = productFilterHeaders;
    }

    public void updateProductFilter(ProductFilterList productFilterList, boolean isPreserveSelection) {
        removeNonExistingFilterItem(productFilterList, isPreserveSelection);
        addNewFilterItem(productFilterList);
    }

    private void removeNonExistingFilterItem(ProductFilterList productFilterList, boolean isPreserveSelection) {
        List<ProductFilterHeader> loadedProductFilterHeaderList = productFilterList.getProductFilterHeaders();
        List<String> indexList = new ArrayList<>();
        for (ProductFilterHeader productFilterHeader :
                loadedProductFilterHeaderList) {
            indexList.add(productFilterHeader.getNameEN());
        }

        ListIterator<ProductFilterHeader> it = this.mProductFilterHeaders.listIterator();
        while (it.hasNext()) {
            ProductFilterHeader productFilterHeader = it.next();
            if (indexList.contains(productFilterHeader.getNameEN())) {
                int matchIndex = indexList.indexOf(productFilterHeader.getNameEN());
                productFilterHeader.replaceExisting(loadedProductFilterHeaderList.get(matchIndex), isPreserveSelection);
            } else {
                it.remove();
            }
        }
    }

    private void addNewFilterItem(ProductFilterList productFilterList) {
        List<String> indexList = new ArrayList<>();
        for (ProductFilterHeader loadedProductFilterHeader :
                this.mProductFilterHeaders) {
            indexList.add(loadedProductFilterHeader.getNameEN());
        }

        for (ProductFilterHeader loadedProductFilterHeader :
                productFilterList.getProductFilterHeaders()) {
            if (!indexList.contains(loadedProductFilterHeader.getNameEN())) {
                this.mProductFilterHeaders.add(loadedProductFilterHeader);
            }
        }
    }

//    public void updateSortOption(ProductSortList productSortList, boolean isPreserveSelection) {
//        ProductFilterHeader productSortHeader = new ProductFilterHeader(0, productSortList.getName(), productSortList.getSlug(), "single", productSortList.getProductFilterItems());
//        if (isProductFilterListAvailable()) {
//            boolean isFound = false;
//            for (ProductFilterHeader productFilterHeader :
//                    mProductFilterHeaders) {
//                if (productFilterHeader.getSlug().equalsIgnoreCase(productSortList.getSlug())) {
//                    productFilterHeader.replaceSortHeader(productSortHeader, isPreserveSelection);
//                    isFound = true;
//                    break;
//                }
//            }
//
//            if (!isFound) {
//                mProductFilterHeaders.add(productSortHeader);
//            }
//        } else {
//            mProductFilterHeaders = new ArrayList<>();
//            mProductFilterHeaders.add(productSortHeader);
//        }
//    }

    public boolean isProductFilterListAvailable() {
        return mProductFilterHeaders != null && !mProductFilterHeaders.isEmpty();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void clearAllSelectedFilters() {
        for (ProductFilterHeader productFilterHeader :
                mProductFilterHeaders) {
            productFilterHeader.clearAllSelectedFilterOptions();
        }
    }

    public Map<String, String> getFilterOptionMap() {
        Map<String, String> filterOptionMap = new HashMap<>();
        String productFilterOption;
        for (ProductFilterHeader productFilterHeader :
                mProductFilterHeaders) {
            productFilterOption = productFilterHeader.getSelectedProductFilterOptionIfAvailable();
            if (!TextUtils.isEmpty(productFilterOption)) {
                filterOptionMap.put(productFilterHeader.getSlug(), productFilterOption);
            }
        }

        return filterOptionMap;
    }
}