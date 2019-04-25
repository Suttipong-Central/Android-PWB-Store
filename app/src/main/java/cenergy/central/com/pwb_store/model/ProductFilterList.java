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
    private List<Category> categories;

    public ProductFilterList(List<Category> categories) {
        this.categories = categories;
    }

    protected ProductFilterList(Parcel in) {
        viewTypeId = in.readInt();
        categories = in.createTypedArrayList(Category.CREATOR);
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(categories);
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

    public List<Category> getProductFilterItems() {
        return categories;
    }

}