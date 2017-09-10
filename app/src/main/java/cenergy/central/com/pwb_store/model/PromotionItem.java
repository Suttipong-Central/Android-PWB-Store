package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionItem implements IViewType,Parcelable {

    private int viewTypeId;
    private String header;
    private String detail;
    private String description;
    private PromotionDetailText mPromotionDetailText;

    public PromotionItem(String header, String detail, String description, PromotionDetailText promotionDetailText){
        this.header = header;
        this.detail = detail;
        this.description = description;
        this.mPromotionDetailText = promotionDetailText;
    }

    public static final Creator<PromotionItem> CREATOR = new Creator<PromotionItem>() {
        @Override
        public PromotionItem createFromParcel(Parcel in) {
            return new PromotionItem(in);
        }

        @Override
        public PromotionItem[] newArray(int size) {
            return new PromotionItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected PromotionItem(Parcel in) {
        viewTypeId = in.readInt();
        header = in.readString();
        detail = in.readString();
        description = in.readString();
        mPromotionDetailText = in.readParcelable(PromotionDetailText.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(header);
        dest.writeString(detail);
        dest.writeString(description);
        dest.writeParcelable(mPromotionDetailText, flags);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PromotionDetailText getPromotionDetailText() {
        return mPromotionDetailText;
    }

    public void setPromotionDetailText(PromotionDetailText promotionDetailText) {
        mPromotionDetailText = promotionDetailText;
    }
}
