package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionPayment implements IViewType,Parcelable{

    private int viewTypeId;
    private List<PromotionPaymentItem> mPromotionPaymentItemList;

    public PromotionPayment(List<PromotionPaymentItem> promotionItems){
        this.mPromotionPaymentItemList = promotionItems;
    }

    public static final Creator<PromotionPayment> CREATOR = new Creator<PromotionPayment>() {
        @Override
        public PromotionPayment createFromParcel(Parcel in) {
            return new PromotionPayment(in);
        }

        @Override
        public PromotionPayment[] newArray(int size) {
            return new PromotionPayment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected PromotionPayment(Parcel in) {
        viewTypeId = in.readInt();
        mPromotionPaymentItemList = in.createTypedArrayList(PromotionPaymentItem.CREATOR);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mPromotionPaymentItemList);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<PromotionPaymentItem> getPromotionPaymentItemList() {
        return mPromotionPaymentItemList;
    }

    public void setPromotionPaymentItemList(List<PromotionPaymentItem> promotionItemList) {
        mPromotionPaymentItemList = promotionItemList;
    }
}
