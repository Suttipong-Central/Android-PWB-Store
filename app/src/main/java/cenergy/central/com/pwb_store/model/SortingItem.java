package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingItem implements IViewType, Parcelable {
    public static final Creator<SortingItem> CREATOR = new Creator<SortingItem>() {
        @Override
        public SortingItem createFromParcel(Parcel in) {
            return new SortingItem(in);
        }

        @Override
        public SortingItem[] newArray(int size) {
            return new SortingItem[size];
        }
    };
    private static final String TAG = "SortingItem";
    private int viewTypeId;
    private int filterId;
    private String filterName;
    private String slug;
    private String value;
    private String parentId;
    private boolean isSelected;

    public SortingItem(int filterId, String filterName, String slug, String value, String parentId, boolean isSelected){
        this.filterId = filterId;
        this.filterName = filterName;
        this.slug = slug;
        this.value = value;
        this.parentId = parentId;
        this.isSelected = isSelected;
    }

    public SortingItem(SortingItem sortingItem) {
        this.filterId = sortingItem.getFilterId();
        this.filterName = sortingItem.getFilterName();
        this.slug = sortingItem.getSlug();
        this.isSelected = sortingItem.isSelected();
    }

    protected SortingItem(Parcel in) {
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
        if (!TextUtils.isEmpty(slug)) {
            return slug;
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
