package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class TextHeader implements IViewType, Parcelable {
    private int viewTypeId;
    private String title;

    public TextHeader(String title) {
        this.title = title;
    }

    protected TextHeader(Parcel in) {
        viewTypeId = in.readInt();
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextHeader> CREATOR = new Creator<TextHeader>() {
        @Override
        public TextHeader createFromParcel(Parcel in) {
            return new TextHeader(in);
        }

        @Override
        public TextHeader[] newArray(int size) {
            return new TextHeader[size];
        }
    };

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}