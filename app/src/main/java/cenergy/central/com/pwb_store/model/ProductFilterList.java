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
    private List<ProductFilterItem> productFilterItems;

    public ProductFilterList(List<ProductFilterItem> productFilterItems) {
        this.productFilterItems = productFilterItems;
    }

//    public ProductFilterList(ProductFilterList productFilterList) {
//        if (productFilterList != null){
//            this.total = productFilterList.getTotal();
//            List<ProductFilterItem> ProductFilterItemList = new ArrayList<>();
//
//            if (productFilterList.isProductFilterListAvailable()) {
//                for (ProductFilterItem ProductFilterItem :
//                        productFilterList.getproductFilterItems()) {
//                    ProductFilterItemList.add(new ProductFilterItem(ProductFilterItem));
//                }
//            }
//
//            this.productFilterItems = ProductFilterItemList;
//        }
//
//    }

    protected ProductFilterList(Parcel in) {
        viewTypeId = in.readInt();
        productFilterItems = in.createTypedArrayList(ProductFilterItem.CREATOR);
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(productFilterItems);
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

    public List<ProductFilterItem> getproductFilterItems() {
        return productFilterItems;
    }

//    public void setproductFilterItems(List<ProductFilterItem> productFilterItems) {
//        productFilterItems = productFilterItems;
//    }
//
//    public void updateProductFilter(ProductFilterList productFilterList, boolean isPreserveSelection) {
//        removeNonExistingFilterItem(productFilterList, isPreserveSelection);
//        addNewFilterItem(productFilterList);
//    }
//
//    private void removeNonExistingFilterItem(ProductFilterList productFilterList, boolean isPreserveSelection) {
//        List<ProductFilterItem> loadedProductFilterItemList = productFilterList.getproductFilterItems();
//        List<String> indexList = new ArrayList<>();
//        for (ProductFilterItem ProductFilterItem :
//                loadedProductFilterItemList) {
//            indexList.add(ProductFilterItem.getFilterName());
//        }
//
//        ListIterator<ProductFilterItem> it = this.productFilterItems.listIterator();
//        while (it.hasNext()) {
//            ProductFilterItem ProductFilterItem = it.next();
//            if (indexList.contains(ProductFilterItem.getFilterName())) {
//                int matchIndex = indexList.indexOf(ProductFilterItem.getFilterName());
//                ProductFilterItem.replaceExisting(loadedProductFilterItemList.get(matchIndex), isPreserveSelection);
//            } else {
//                it.remove();
//            }
//        }
//    }
//
//    private void addNewFilterItem(ProductFilterList productFilterList) {
//        List<String> indexList = new ArrayList<>();
//        for (ProductFilterItem loadedProductFilterItem :
//                this.productFilterItems) {
//            indexList.add(loadedProductFilterItem.getName());
//        }
//
//        for (ProductFilterItem loadedProductFilterItem :
//                productFilterList.getproductFilterItems()) {
//            if (!indexList.contains(loadedProductFilterItem.getName())) {
//                this.productFilterItems.add(loadedProductFilterItem);
//            }
//        }
//    }
//
////    public void updateSortOption(ProductSortList productSortList, boolean isPreserveSelection) {
////        ProductFilterItem productSortHeader = new ProductFilterItem(0, productSortList.getName(), productSortList.getSlug(), "single", productSortList.getProductFilterItems());
////        if (isProductFilterListAvailable()) {
////            boolean isFound = false;
////            for (ProductFilterItem ProductFilterItem :
////                    productFilterItems) {
////                if (ProductFilterItem.getSlug().equalsIgnoreCase(productSortList.getSlug())) {
////                    ProductFilterItem.replaceSortHeader(productSortHeader, isPreserveSelection);
////                    isFound = true;
////                    break;
////                }
////            }
////
////            if (!isFound) {
////                productFilterItems.add(productSortHeader);
////            }
////        } else {
////            productFilterItems = new ArrayList<>();
////            productFilterItems.add(productSortHeader);
////        }
////    }
//
//    public boolean isProductFilterListAvailable() {
//        return productFilterItems != null && !productFilterItems.isEmpty();
//    }
//
//    public int getTotal() {
//        return total;
//    }
//
//    public void setTotal(int total) {
//        this.total = total;
//    }
//
//    public void clearAllSelectedFilters() {
//        for (ProductFilterItem ProductFilterItem :
//                productFilterItems) {
//            ProductFilterItem.clearAllSelectedFilterOptions();
//        }
//    }
//
//    public Map<String, String> getFilterOptionMap() {
//        Map<String, String> filterOptionMap = new HashMap<>();
//        String productFilterOption;
//        for (ProductFilterItem ProductFilterItem :
//                productFilterItems) {
//            productFilterOption = ProductFilterItem.getSelectedProductFilterOptionIfAvailable();
//            if (!TextUtils.isEmpty(productFilterOption)) {
//                filterOptionMap.put(ProductFilterItem.getName(), productFilterOption);
//            }
//        }
//
//        return filterOptionMap;
//    }
}