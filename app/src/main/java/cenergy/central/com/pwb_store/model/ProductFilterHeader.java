package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterHeader extends RealmObject implements IViewType, Parcelable {
    public static final Creator<ProductFilterHeader> CREATOR = new Creator<ProductFilterHeader>() {
        @Override
        public ProductFilterHeader createFromParcel(Parcel in) {
            return new ProductFilterHeader(in);
        }

        @Override
        public ProductFilterHeader[] newArray(int size) {
            return new ProductFilterHeader[size];
        }
    };
    private static final String TAG = "ProductFilterHeader";
    @Ignore
    private int viewTypeId;
    @SerializedName("entity_id")
    @Expose
    private String id;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("category_tablet_icon")
    @Expose
    private String imageURL;
    @SerializedName("include_in_menu")
    @Expose
    private int includeInMenu;
    private String type;
    private int position;
    @SerializedName("children_data")
    @Expose
    private RealmList<ProductFilterSubHeader> mProductFilterSubHeaders;
    private boolean isExpanded;

//    public ProductFilterHeader(int departmentId, int rootDeptId, int id, String name, String nameEN, String slug,
//                               String metaDescription, String urlName, String urlNameEN, String type,
//                               List<ProductFilterItem> mProductFilterItemList) {
//        this.departmentId = departmentId;
//        this.rootDeptId = rootDeptId;
//        this.id = id;
//        this.name = name;
//        this.slug = slug;
//        this.type = type;
//        this.mProductFilterItemList = mProductFilterItemList;
//        this.isExpanded = false;
//        this.nameEN = nameEN;
//        this.metaDescription = metaDescription;
//        this.urlName = urlName;
//        this.urlNameEN = urlNameEN;
//    }

    public ProductFilterHeader () {

    }

    public ProductFilterHeader(String id, String name, String level, String imageURL, String type, List<ProductFilterSubHeader> productFilterSubHeaders) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.isExpanded = false;
        this.imageURL = imageURL;
        this.type = type;
    }


    protected ProductFilterHeader(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readString();
        level = in.readString();
        name = in.readString();
        imageURL = in.readString();
        includeInMenu = in.readInt();
        type = in.readString();
        position = in.readInt();
        isExpanded = in.readByte() != 0;
        this.mProductFilterSubHeaders = new RealmList<>();
        this.mProductFilterSubHeaders.addAll(in.createTypedArrayList(ProductFilterSubHeader.CREATOR));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(id);
        dest.writeString(level);
        dest.writeString(name);
        dest.writeString(imageURL);
        dest.writeInt(includeInMenu);
        dest.writeString(type);
        dest.writeInt(position);
        dest.writeByte((byte) (isExpanded ? 1 : 0));
        dest.writeTypedList(mProductFilterSubHeaders);
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

    public List<ProductFilterSubHeader> getProductFilterSubHeaders() {
        return mProductFilterSubHeaders;
    }

    public void setProductFilterSubHeaders(RealmList<ProductFilterSubHeader> mProductFilterSubHeaders) {
        this.mProductFilterSubHeaders = mProductFilterSubHeaders;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getIncludeInMenu() {
        return includeInMenu;
    }

    public boolean IsIncludeInMenu() {
        return includeInMenu == 1;
    }

    public void setIncludeInMenu(int includeInMenu) {
        this.includeInMenu = includeInMenu;
    }
}