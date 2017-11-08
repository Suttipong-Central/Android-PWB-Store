package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionPaymentItem implements IViewType,Parcelable {

    private int viewTypeId;
    private String header;
    @SerializedName("promotion_name")
    @Expose
    private String detail;
    private String description;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    private PromotionDetailText mPromotionDetailText;

    public PromotionPaymentItem(String detail,String startDate, String endDate, PromotionDetailText promotionDetailText){
        this.detail = detail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mPromotionDetailText = promotionDetailText;
    }

    public static final Creator<PromotionPaymentItem> CREATOR = new Creator<PromotionPaymentItem>() {
        @Override
        public PromotionPaymentItem createFromParcel(Parcel in) {
            return new PromotionPaymentItem(in);
        }

        @Override
        public PromotionPaymentItem[] newArray(int size) {
            return new PromotionPaymentItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected PromotionPaymentItem(Parcel in) {
        viewTypeId = in.readInt();
        header = in.readString();
        detail = in.readString();
        description = in.readString();
        mPromotionDetailText = in.readParcelable(PromotionDetailText.class.getClassLoader());
        startDate = in.readString();
        endDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(header);
        dest.writeString(detail);
        dest.writeString(description);
        dest.writeParcelable(mPromotionDetailText, flags);
        dest.writeString(startDate);
        dest.writeString(endDate);
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
