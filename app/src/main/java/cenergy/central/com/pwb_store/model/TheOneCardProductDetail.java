package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class TheOneCardProductDetail implements IViewType, Parcelable {

    public static final Creator<TheOneCardProductDetail> CREATOR = new Creator<TheOneCardProductDetail>() {
        @Override
        public TheOneCardProductDetail createFromParcel(Parcel in) {
            return new TheOneCardProductDetail(in);
        }

        @Override
        public TheOneCardProductDetail[] newArray(int size) {
            return new TheOneCardProductDetail[size];
        }
    };
    private int viewTypeId;
    private int point;
    private int cash;

    public TheOneCardProductDetail(int point, int cash) {
        this.point = point;
        this.cash = cash;
    }

    protected TheOneCardProductDetail(Parcel in) {
        viewTypeId = in.readInt();
        point = in.readInt();
        cash = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(point);
        dest.writeInt(cash);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
