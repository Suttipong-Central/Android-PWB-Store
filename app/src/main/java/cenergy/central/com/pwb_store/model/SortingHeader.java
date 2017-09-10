package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingHeader implements IViewType, Parcelable {
    public static final Creator<SortingHeader> CREATOR = new Creator<SortingHeader>() {
        @Override
        public SortingHeader createFromParcel(Parcel in) {
            return new SortingHeader(in);
        }

        @Override
        public SortingHeader[] newArray(int size) {
            return new SortingHeader[size];
        }
    };
    private static final String TAG = "SortingHeader";
    private int viewTypeId;
    private String id;
    private String name;
    private String slug;
    private String type;
    private List<SortingItem> mSortingItems = new ArrayList<>();
    private boolean isExpanded;

    public SortingHeader(String id, String name, String slug, String type, List<SortingItem> sortingItems) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.mSortingItems = sortingItems;
        this.isExpanded = false;
    }

    public SortingHeader(SortingHeader sortingHeader) {
        this.id = sortingHeader.getId();
        this.name = sortingHeader.getName();
        this.slug = sortingHeader.getSlug();
        this.type = sortingHeader.getType();

        List<SortingItem> sortingItems = new ArrayList<>();
        if (sortingHeader.isSortingItemListAvailable())
            for (SortingItem sortingItem :
                    sortingHeader.getSortingItems()) {
                sortingItems.add(new SortingItem(sortingItem));
            }

        this.mSortingItems = sortingItems;
        this.isExpanded = false;
    }

    protected SortingHeader(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readString();
        name = in.readString();
        slug = in.readString();
        type = in.readString();
        mSortingItems = in.createTypedArrayList(SortingItem.CREATOR);
        isExpanded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(type);
        dest.writeTypedList(mSortingItems);
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

    public List<SortingItem> getSortingItems() {
        return mSortingItems;
    }

    public void setSortingItems(List<SortingItem> sortingItems) {
        mSortingItems = sortingItems;
    }

    public boolean isSortingItemListAvailable() {
        return mSortingItems != null && !mSortingItems.isEmpty();
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
        for (SortingItem sortingItem :
                mSortingItems) {
            sortingItem.setSelected(false);
        }
    }

    public void replaceSortHeader(SortingHeader sortingHeader, boolean isPreserveSelection) {
        this.id = sortingHeader.getId();
        this.name = sortingHeader.getName();
        this.slug = sortingHeader.getSlug();
        this.type = sortingHeader.getType();
        if (isPreserveSelection) {
            for (SortingItem sortingItem :
                    mSortingItems) {
                for (SortingItem newSortingItem :
                        sortingHeader.getSortingItems()) {
                    if (sortingItem.getSlug().equalsIgnoreCase(newSortingItem.getSlug())) {
                        newSortingItem.setSelected(sortingItem.isSelected());
                        break;
                    }
                }
            }
        }

        this.mSortingItems = sortingHeader.getSortingItems();
    }

    public void replaceExisting(SortingHeader loadedSortingHeader, boolean isPreserveSelection) {
        this.id = loadedSortingHeader.getId();
        this.name = loadedSortingHeader.getName();
        this.slug = loadedSortingHeader.getSlug();
        this.type = loadedSortingHeader.getType();

        if (isPreserveSelection) {
            for (SortingItem sortingItem :
                    mSortingItems) {
                for (SortingItem newSortingItem :
                        loadedSortingHeader.getSortingItems()) {
                    if (sortingItem.getSlug().equalsIgnoreCase(newSortingItem.getSlug())) {
                        newSortingItem.setSelected(sortingItem.isSelected());
                        break;
                    }
                }
            }
        }

        this.mSortingItems = loadedSortingHeader.getSortingItems();
    }

    public String getSelectedSortingOptionIfAvailable() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SortingItem sortingItem :
                mSortingItems) {
            if (sortingItem.isSelected()) {
                stringBuilder.append(sortingItem.getSlug());
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