package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class StoreFilterHeader implements IViewType, Parcelable {
    public static final Creator<StoreFilterHeader> CREATOR = new Creator<StoreFilterHeader>() {
        @Override
        public StoreFilterHeader createFromParcel(Parcel in) {
            return new StoreFilterHeader(in);
        }

        @Override
        public StoreFilterHeader[] newArray(int size) {
            return new StoreFilterHeader[size];
        }
    };
    private static final String TAG = "StoreFilterHeader";
    private int viewTypeId;
    private String id;
    private String name;
    private String slug;
    private String type;
    private List<StoreFilterItem> mStoreFilterItems = new ArrayList<>();
    private boolean isExpanded;

    public StoreFilterHeader(String id, String name, String slug, String type, List<StoreFilterItem> storeFilterItems) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.mStoreFilterItems = storeFilterItems;
        this.isExpanded = false;
    }

    public StoreFilterHeader(StoreFilterHeader storeFilterHeader) {
        this.id = storeFilterHeader.getId();
        this.name = storeFilterHeader.getName();
        this.slug = storeFilterHeader.getSlug();
        this.type = storeFilterHeader.getType();

        List<StoreFilterItem> storeFilterItemList = new ArrayList<>();
        if (storeFilterHeader.isStoreFilterItemListAvailable())
            for (StoreFilterItem storeFilterItem :
                    storeFilterHeader.getStoreFilterItemList()) {
                storeFilterItemList.add(new StoreFilterItem(storeFilterItem));
            }

        this.mStoreFilterItems = storeFilterItemList;
        this.isExpanded = false;
    }

    protected StoreFilterHeader(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readString();
        name = in.readString();
        slug = in.readString();
        type = in.readString();
        mStoreFilterItems = in.createTypedArrayList(StoreFilterItem.CREATOR);
        isExpanded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(type);
        dest.writeTypedList(mStoreFilterItems);
        dest.writeByte((byte) (isExpanded ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSortBy() {
        return name.equalsIgnoreCase("เรียงสินค้าตาม");
    }

    public boolean isMultipleType() {
        return type.equalsIgnoreCase("multiple");
    }

    public List<StoreFilterItem> getStoreFilterItemList() {
        return mStoreFilterItems;
    }

    public void setStoreFilterItemList(List<StoreFilterItem> mStoreFilterItems) {
        this.mStoreFilterItems = mStoreFilterItems;
    }

    public boolean isStoreFilterItemListAvailable() {
        return mStoreFilterItems != null && !mStoreFilterItems.isEmpty();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void clearAllSelectedFilterOptions() {
        for (StoreFilterItem storeFilterItem :
                mStoreFilterItems) {
            storeFilterItem.setSelected(false);
        }
    }

    public void replaceSortHeader(StoreFilterHeader storeFilterHeader, boolean isPreserveSelection) {
        this.id = storeFilterHeader.getId();
        this.name = storeFilterHeader.getName();
        this.slug = storeFilterHeader.getSlug();
        this.type = storeFilterHeader.getType();
        if (isPreserveSelection) {
            for (StoreFilterItem storeFilterItem :
                    mStoreFilterItems) {
                for (StoreFilterItem newStoreFilterItem :
                        storeFilterHeader.getStoreFilterItemList()) {
                    if (storeFilterItem.getSlug().equalsIgnoreCase(newStoreFilterItem.getSlug())) {
                        newStoreFilterItem.setSelected(storeFilterItem.isSelected());
                        break;
                    }
                }
            }
        }

        this.mStoreFilterItems = storeFilterHeader.getStoreFilterItemList();
    }

    public void replaceExisting(StoreFilterHeader loadedStoreFilterHeader, boolean isPreserveSelection) {
        this.id = loadedStoreFilterHeader.getId();
        this.name = loadedStoreFilterHeader.getName();
        this.slug = loadedStoreFilterHeader.getSlug();
        this.type = loadedStoreFilterHeader.getType();

        if (isPreserveSelection) {
            for (StoreFilterItem storeFilterItem :
                    mStoreFilterItems) {
                for (StoreFilterItem newStoreFilterItem :
                        loadedStoreFilterHeader.getStoreFilterItemList()) {
                    if (storeFilterItem.getSlug().equalsIgnoreCase(newStoreFilterItem.getSlug())) {
                        newStoreFilterItem.setSelected(storeFilterItem.isSelected());
                        break;
                    }
                }
            }
        }

        this.mStoreFilterItems = loadedStoreFilterHeader.getStoreFilterItemList();
    }

    public String getSelectedStoreFilterOptionIfAvailable() {
        StringBuilder stringBuilder = new StringBuilder();

        for (StoreFilterItem storeFilterItem :
                mStoreFilterItems) {
            if (storeFilterItem.isSelected()) {
                stringBuilder.append(storeFilterItem.getSlug());
                stringBuilder.append(",");
            }
        }

        String resultString = stringBuilder.toString();

        if (TextUtils.isEmpty(resultString)) {
            return "";
        }

        return resultString.substring(0, resultString.length() - 1);
    }
}