package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class CategoryDao implements IViewType, Parcelable {

    private int viewTypeId;

    private List<Category> mCategoryList = new ArrayList<>();

    public static final Creator<CategoryDao> CREATOR = new Creator<CategoryDao>() {
        @Override
        public CategoryDao createFromParcel(Parcel in) {
            return new CategoryDao(in);
        }

        @Override
        public CategoryDao[] newArray(int size) {
            return new CategoryDao[size];
        }
    };

    public CategoryDao(List<Category> categoryList) {
        this.mCategoryList = categoryList;
    }

    protected CategoryDao(Parcel in) {
        viewTypeId = in.readInt();
        mCategoryList = in.createTypedArrayList(Category.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mCategoryList);
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

    public List<Category> getCategoryList() {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        mCategoryList = categoryList;
    }
}
