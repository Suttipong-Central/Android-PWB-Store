package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 9/7/2017 AD.
 */

public class StoreDao implements IViewType,Parcelable {
    private int viewTypeId;
    private List<StoreList> mStoreLists = new ArrayList<>();
    private StoreList selectStore;

    protected StoreDao(Parcel in) {
        viewTypeId = in.readInt();
        mStoreLists = in.readArrayList(StoreList.class.getClassLoader());
        selectStore = in.readParcelable(StoreList.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mStoreLists);
        dest.writeParcelable(selectStore, flags);
    }


    public static final Creator<StoreDao> CREATOR = new Creator<StoreDao>() {
        @Override
        public StoreDao createFromParcel(Parcel in) {
            return new StoreDao(in);
        }

        @Override
        public StoreDao[] newArray(int size) {
            return new StoreDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public StoreDao(List<StoreList> storeLists){
        this.mStoreLists = storeLists;
    }

    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<StoreList> getStoreLists() {
        return mStoreLists;
    }

    public void setStoreLists(List<StoreList> storeLists) {
        mStoreLists = storeLists;
    }

    public StoreList getSelectStore() {
        return selectStore;
    }

    public void setSelectStore(StoreList selectStore) {
        this.selectStore = selectStore;
    }

    public boolean isSelectedStoreAvailable() {
        return selectStore != null;
    }

    public boolean isStoreEmpty() {
        return mStoreLists.isEmpty();
    }

    public boolean isSelectedStoreTheSame(StoreList selectedStore) {
        return isSelectedStoreAvailable() && this.selectStore.getStoreId().equalsIgnoreCase(selectedStore.getStoreId());
    }

    public boolean isSingleStoreAvailable() {
        if (mStoreLists != null && mStoreLists.size() == 1) {
            selectStore = mStoreLists.get(0);

            return true;
        }

        return false;
    }

    public boolean setSelectedStoreIfAvailable(StoreList selectedStore) {
        for (StoreList storeItem :
                mStoreLists) {
            if (storeItem.getStoreId().equalsIgnoreCase(selectedStore.getStoreId())) {
                this.selectStore = storeItem;
                return true;
            }
        }

        return false;
    }

    public boolean isStoreListItemListAvailable() {
        return mStoreLists != null && !mStoreLists.isEmpty();
    }
}
