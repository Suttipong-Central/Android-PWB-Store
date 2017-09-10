package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class StoreFilterItem implements IViewType, Parcelable {
    public static final Creator<StoreFilterItem> CREATOR = new Creator<StoreFilterItem>() {
        @Override
        public StoreFilterItem createFromParcel(Parcel in) {
            return new StoreFilterItem(in);
        }

        @Override
        public StoreFilterItem[] newArray(int size) {
            return new StoreFilterItem[size];
        }
    };
    private static final String TAG = "StoreFilterItem";
    private int viewTypeId;
    private int filterId;
    private String filterName;
    private String slug;
    private String value;
    private String parentId;
    private boolean isSelected;

    public StoreFilterItem(int filterId, String filterName, String slug, String value, String parentId, boolean isSelected){
        this.filterId = filterId;
        this.filterName = filterName;
        this.slug = slug;
        this.value = value;
        this.parentId = parentId;
        this.isSelected = isSelected;
    }

    public StoreFilterItem(StoreFilterItem productFilterItem) {
        this.filterId = productFilterItem.getFilterId();
        this.filterName = productFilterItem.getFilterName();
        this.slug = productFilterItem.getSlug();
        this.isSelected = productFilterItem.isSelected();
    }

    protected StoreFilterItem(Parcel in) {
        viewTypeId = in.readInt();
        filterId = in.readInt();
        filterName = in.readString();
        slug = in.readString();
        parentId = in.readString();
        isSelected = in.readByte() != 0;
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(filterId);
        dest.writeString(filterName);
        dest.writeString(slug);
        dest.writeString(parentId);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(value);
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
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSlug() {
        if (!TextUtils.isEmpty(value)) {
            return value;
        }

        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
