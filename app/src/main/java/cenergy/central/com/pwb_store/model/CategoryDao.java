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
    private Category category;

    public CategoryDao(List<Category> categoryList) {
        this.mCategoryList = categoryList;
    }

    public CategoryDao(Category category) {
        mCategoryList.add(category);
    }

    protected CategoryDao(Parcel in) {
        viewTypeId = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        mCategoryList = new ArrayList<>();
        in.readList(mCategoryList, Category.class.getClassLoader());
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(viewTypeId);
        parcel.writeTypedList(mCategoryList);
        parcel.writeParcelable(category, i);
    }
}
