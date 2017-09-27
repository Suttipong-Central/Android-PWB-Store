package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class TimeSlotItem implements IViewType,Parcelable {

    private int viewTypeId;
    @SerializedName("slotID")
    @Expose
    private int slotId;
    @SerializedName("slotLabel")
    @Expose
    private String slotLabel;
    @SerializedName("availabel")
    @Expose
    private int availabel;

    protected TimeSlotItem(Parcel in) {
        viewTypeId = in.readInt();
        slotId = in.readInt();
        slotLabel = in.readString();
        availabel = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(slotId);
        dest.writeString(slotLabel);
        dest.writeInt(availabel);
    }

    public TimeSlotItem(int slotId, String slotLabel, int availabel){
        this.slotId = slotId;
        this.slotLabel = slotLabel;
        this.availabel = availabel;
    }

    public TimeSlotItem(List<TimeSlotItem> timeSlotItems){
        for (TimeSlotItem timeSlotItem : timeSlotItems){
            this.slotId = timeSlotItem.getSlotId();
            this.slotLabel = timeSlotItem.getSlotLabel();
            this.availabel = timeSlotItem.getAvailabel();
        }
    }

    public static final Creator<TimeSlotItem> CREATOR = new Creator<TimeSlotItem>() {
        @Override
        public TimeSlotItem createFromParcel(Parcel in) {
            return new TimeSlotItem(in);
        }

        @Override
        public TimeSlotItem[] newArray(int size) {
            return new TimeSlotItem[size];
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

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getSlotLabel() {
        return slotLabel;
    }

    public void setSlotLabel(String slotLabel) {
        this.slotLabel = slotLabel;
    }

    public int getAvailabel() {
        return availabel;
    }

    public void setAvailabel(int availabel) {
        this.availabel = availabel;
    }
}
