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
    private List<ProductFilterSubHeader> mProductFilterSubHeaders;

    public ProductFilterList(List<ProductFilterSubHeader> productFilterSubHeaders) {
        this.mProductFilterSubHeaders = productFilterSubHeaders;
    }

    public ProductFilterList(ProductFilterList productFilterList) {
        if (productFilterList != null){
            this.total = productFilterList.getTotal();
            List<ProductFilterSubHeader> productFilterSubHeaderList = new ArrayList<>();

            if (productFilterList.isProductFilterListAvailable()) {
                for (ProductFilterSubHeader productFilterSubHeader :
                        productFilterList.getProductFilterSubHeaders()) {
                    productFilterSubHeaderList.add(new ProductFilterSubHeader(productFilterSubHeader));
                }
            }

            this.mProductFilterSubHeaders = productFilterSubHeaderList;
        }

    }

    protected ProductFilterList(Parcel in) {
        viewTypeId = in.readInt();
        mProductFilterSubHeaders = in.createTypedArrayList(ProductFilterSubHeader.CREATOR);
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(mProductFilterSubHeaders);
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

    public List<ProductFilterSubHeader> getProductFilterSubHeaders() {
        return mProductFilterSubHeaders;
    }

    public void setProductFilterSubHeaders(List<ProductFilterSubHeader> productFilterSubHeaders) {
        mProductFilterSubHeaders = productFilterSubHeaders;
    }

    public void updateProductFilter(ProductFilterList productFilterList, boolean isPreserveSelection) {
        removeNonExistingFilterItem(productFilterList, isPreserveSelection);
        addNewFilterItem(productFilterList);
    }

    private void removeNonExistingFilterItem(ProductFilterList productFilterList, boolean isPreserveSelection) {
        List<ProductFilterSubHeader> loadedProductFilterSubHeaderList = productFilterList.getProductFilterSubHeaders();
        List<String> indexList = new ArrayList<>();
        for (ProductFilterSubHeader productFilterSubHeader :
                loadedProductFilterSubHeaderList) {
            indexList.add(productFilterSubHeader.getName());
        }

        ListIterator<ProductFilterSubHeader> it = this.mProductFilterSubHeaders.listIterator();
        while (it.hasNext()) {
            ProductFilterSubHeader productFilterSubHeader = it.next();
            if (indexList.contains(productFilterSubHeader.getName())) {
                int matchIndex = indexList.indexOf(productFilterSubHeader.getName());
                productFilterSubHeader.replaceExisting(loadedProductFilterSubHeaderList.get(matchIndex), isPreserveSelection);
            } else {
                it.remove();
            }
        }
    }

    private void addNewFilterItem(ProductFilterList productFilterList) {
        List<String> indexList = new ArrayList<>();
        for (ProductFilterSubHeader loadedProductFilterSubHeader :
                this.mProductFilterSubHeaders) {
            indexList.add(loadedProductFilterSubHeader.getName());
        }

        for (ProductFilterSubHeader loadedProductFilterSubHeader :
                productFilterList.getProductFilterSubHeaders()) {
            if (!indexList.contains(loadedProductFilterSubHeader.getName())) {
                this.mProductFilterSubHeaders.add(loadedProductFilterSubHeader);
            }
        }
    }

//    public void updateSortOption(ProductSortList productSortList, boolean isPreserveSelection) {
//        ProductFilterSubHeader productSortHeader = new ProductFilterSubHeader(0, productSortList.getName(), productSortList.getSlug(), "single", productSortList.getProductFilterItems());
//        if (isProductFilterListAvailable()) {
//            boolean isFound = false;
//            for (ProductFilterSubHeader productFilterSubHeader :
//                    mProductFilterSubHeaders) {
//                if (productFilterSubHeader.getSlug().equalsIgnoreCase(productSortList.getSlug())) {
//                    productFilterSubHeader.replaceSortHeader(productSortHeader, isPreserveSelection);
//                    isFound = true;
//                    break;
//                }
//            }
//
//            if (!isFound) {
//                mProductFilterSubHeaders.add(productSortHeader);
//            }
//        } else {
//            mProductFilterSubHeaders = new ArrayList<>();
//            mProductFilterSubHeaders.add(productSortHeader);
//        }
//    }

    public boolean isProductFilterListAvailable() {
        return mProductFilterSubHeaders != null && !mProductFilterSubHeaders.isEmpty();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void clearAllSelectedFilters() {
        for (ProductFilterSubHeader productFilterSubHeader :
                mProductFilterSubHeaders) {
            productFilterSubHeader.clearAllSelectedFilterOptions();
        }
    }

    public Map<String, String> getFilterOptionMap() {
        Map<String, String> filterOptionMap = new HashMap<>();
        String productFilterOption;
        for (ProductFilterSubHeader productFilterSubHeader :
                mProductFilterSubHeaders) {
            productFilterOption = productFilterSubHeader.getSelectedProductFilterOptionIfAvailable();
            if (!TextUtils.isEmpty(productFilterOption)) {
                filterOptionMap.put(productFilterSubHeader.getName(), productFilterOption);
            }
        }

        return filterOptionMap;
    }
}