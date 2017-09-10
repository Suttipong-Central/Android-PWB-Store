package cenergy.central.com.pwb_store.model;

import java.util.ArrayList;

/**
 * Created by napabhat on 8/22/2017 AD.
 */
public class Event {
    private String date;
    private String count;
    private ArrayList<EventData> eventData;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public ArrayList<EventData> getEventData() {
        return eventData;
    }

    public void setEventData(ArrayList<EventData> eventData) {
        this.eventData = eventData;
    }
}
