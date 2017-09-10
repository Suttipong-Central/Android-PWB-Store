package cenergy.central.com.pwb_store.model;

import java.util.ArrayList;

/**
 * Created by napabhat on 8/22/2017 AD.
 */
public class EventData {
    private String section;
    private ArrayList<dataAboutDate> data;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public ArrayList<dataAboutDate> getData() {
        return data;
    }

    public void setData(ArrayList<dataAboutDate> data) {
        this.data = data;
    }
}
