package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchSuggestionItem implements IViewType, Parcelable {
    private transient int viewTypeId;
//    @SerializedName("id")
//    @Expose
    private int id;
//    @SerializedName("slug")
//    @Expose
    private String slug;
//    @SerializedName("name")
//    @Expose
    private String name;
//    @SerializedName("type")
//    @Expose
    private String type;
//    @SerializedName("description")
//    @Expose
    private String description;

    public SearchSuggestionItem(int id, String slug, String name, String type, String description) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    protected SearchSuggestionItem(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        slug = in.readString();
        name = in.readString();
        type = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeString(slug);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchSuggestionItem> CREATOR = new Creator<SearchSuggestionItem>() {
        @Override
        public SearchSuggestionItem createFromParcel(Parcel in) {
            return new SearchSuggestionItem(in);
        }

        @Override
        public SearchSuggestionItem[] newArray(int size) {
            return new SearchSuggestionItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public boolean isTypeShop() {
        return type.equalsIgnoreCase("shop");
    }

    public boolean isTypeBoutiqueOrShopOrCategory() {
        return type.equalsIgnoreCase("boutique") || type.equalsIgnoreCase("shop") || type.equalsIgnoreCase("category");
    }
}
