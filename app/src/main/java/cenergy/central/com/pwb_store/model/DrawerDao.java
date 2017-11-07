package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 9/7/2017 AD.
 */

public class DrawerDao implements IViewType,Parcelable {
    private int viewTypeId;
    private List<DrawerItem> mDrawerItems = new ArrayList<>();
    private StoreDao mStoreDao;


    protected DrawerDao(Parcel in) {
        viewTypeId = in.readInt();
        mDrawerItems = in.readArrayList(DrawerItem.class.getClassLoader());
        mStoreDao = in.readParcelable(StoreDao.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mDrawerItems);
        dest.writeParcelable(mStoreDao, flags);
    }

    public static final Creator<DrawerDao> CREATOR = new Creator<DrawerDao>() {
        @Override
        public DrawerDao createFromParcel(Parcel in) {
            return new DrawerDao(in);
        }

        @Override
        public DrawerDao[] newArray(int size) {
            return new DrawerDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public DrawerDao(List<DrawerItem> drawerItems){
        this.mDrawerItems = drawerItems;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public List<DrawerItem> getDrawerItems() {
        return mDrawerItems;
    }

    public void setDrawerItems(List<DrawerItem> drawerItems) {
        mDrawerItems = drawerItems;
    }

    public StoreDao getStoreDao() {
        return mStoreDao;
    }

    public void setStoreDao(StoreDao storeDao) {
        mStoreDao = storeDao;
    }

}
