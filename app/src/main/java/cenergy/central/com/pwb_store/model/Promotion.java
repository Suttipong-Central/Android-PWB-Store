package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class Promotion implements IViewType,Parcelable{

    private int viewTypeId;
    private List<PromotionItem> mPromotionItemList;

    public Promotion(List<PromotionItem> promotionItems){
        this.mPromotionItemList = promotionItems;
    }

    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Promotion(Parcel in) {
        viewTypeId = in.readInt();
        mPromotionItemList = in.createTypedArrayList(PromotionItem.CREATOR);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mPromotionItemList);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<PromotionItem> getPromotionItemList() {
        return mPromotionItemList;
    }

    public void setPromotionItemList(List<PromotionItem> promotionItemList) {
        mPromotionItemList = promotionItemList;
    }
}
