package cenergy.central.com.pwb_store.model;

import java.util.Date;

/**
 * Created by napabhat on 8/18/2017 AD.
 */

public class EventObjects {
    private int id;
    private String message;
    private Date date;

    public EventObjects(String message, Date date) {
        this.message = message;
        this.date = date;
    }
    public EventObjects(int id, String message, Date date) {
        this.date = date;
        this.message = message;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
    public Date getDate() {
        return date;
    }
}
