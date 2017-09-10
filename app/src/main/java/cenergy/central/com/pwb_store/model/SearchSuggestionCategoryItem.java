package cenergy.central.com.pwb_store.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import cenergy.central.com.pwb_store.utils.CollectionsUtils;

public class SearchSuggestionCategoryItem implements IViewType, Parcelable {
    private transient int viewTypeId;
//    @SerializedName("id")
//    @Expose
    private int id;
//    @SerializedName("name")
//    @Expose
    private String name;
//    @SerializedName("item_count")
//    @Expose
    private int itemCount;
//    @SerializedName("img_url")
//    @Expose
    private String imgUrl;
    private String query;

    public SearchSuggestionCategoryItem(int id, String name, int itemCount, String imgUrl) {
        this.id = id;
        this.name = name;
        this.itemCount = itemCount;
        this.imgUrl = imgUrl;
    }

    protected SearchSuggestionCategoryItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        itemCount = in.readInt();
        imgUrl = in.readString();
        query = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(itemCount);
        dest.writeString(imgUrl);
        dest.writeString(query);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchSuggestionCategoryItem> CREATOR = new Creator<SearchSuggestionCategoryItem>() {
        @Override
        public SearchSuggestionCategoryItem createFromParcel(Parcel in) {
            return new SearchSuggestionCategoryItem(in);
        }

        @Override
        public SearchSuggestionCategoryItem[] newArray(int size) {
            return new SearchSuggestionCategoryItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Bundle getQueryBundleString() {
        Map<String, String> map = new HashMap<>();
        map.put("category", String.valueOf(id));
        return CollectionsUtils.toStringBundle(map);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }
}
