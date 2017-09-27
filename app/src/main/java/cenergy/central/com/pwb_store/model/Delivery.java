package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class Delivery implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("deliveryDate")
    @Expose
    private String deliveryDate;
    @SerializedName("timeSlot")
    @Expose
    private List<TimeSlotItem> mTimeSlotItems = new ArrayList<>();

    protected Delivery(Parcel in) {
        viewTypeId = in.readInt();
        deliveryDate = in.readString();
        mTimeSlotItems = in.createTypedArrayList(TimeSlotItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(deliveryDate);
        dest.writeTypedList(mTimeSlotItems);
    }

    public Delivery(String delivery, List<TimeSlotItem> timeSlotItems){
        this.deliveryDate = delivery;
        this.mTimeSlotItems = timeSlotItems;
    }

    public static final Creator<Delivery> CREATOR = new Creator<Delivery>() {
        @Override
        public Delivery createFromParcel(Parcel in) {
            return new Delivery(in);
        }

        @Override
        public Delivery[] newArray(int size) {
            return new Delivery[size];
        }
    };

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

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<TimeSlotItem> getTimeSlotItems() {
        return mTimeSlotItems;
    }

    public void setTimeSlotItems(List<TimeSlotItem> timeSlotItems) {
        mTimeSlotItems = timeSlotItems;
    }
}
