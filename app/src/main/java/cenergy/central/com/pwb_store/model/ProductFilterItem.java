package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterItem extends RealmObject implements IViewType, Parcelable {
    public static final Creator<ProductFilterItem> CREATOR = new Creator<ProductFilterItem>() {
        @Override
        public ProductFilterItem createFromParcel(Parcel in) {
            return new ProductFilterItem(in);
        }

        @Override
        public ProductFilterItem[] newArray(int size) {
            return new ProductFilterItem[size];
        }
    };
    private static final String TAG = "ProductFilterItem";
    @Ignore
    private int viewTypeId;
    
    private String id;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName(value = "UrlName", alternate = "image")
    @Expose
    private String urlName;
    @SerializedName(value = "DepartmentName", alternate = "name")
    @Expose
    private String filterName;
//
//    @SerializedName("name")
//    @Expose
    private String slug;

    private String value;
    
    @Ignore
    private boolean isSelected;

    private int filterId;

    public ProductFilterItem() {
    }

    public ProductFilterItem(ProductFilterItem productFilterItem) {
        this.filterId = productFilterItem.getFilterId();
        this.filterName = productFilterItem.getFilterName();
        this.slug = productFilterItem.getSlug();
        this.isSelected = productFilterItem.isSelected();
    }

    protected ProductFilterItem(Parcel in) {
        viewTypeId = in.readInt();
        filterId = in.readInt();
        filterName = in.readString();
        slug = in.readString();
        isSelected = in.readByte() != 0;
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(filterId);
        dest.writeString(filterName);
        dest.writeString(slug);
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }
}
