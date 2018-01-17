package cenergy.central.com.pwb_store.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by napabhat on 1/12/2017 AD.
 */

public class DateUtils {
    public static String getDisplayDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static String getDisplayDateFromString(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
        Date convertedDate;
        try {
            convertedDate = simpleDateFormat.parse(dateString);
        } catch (NullPointerException | ParseException ex) {
            return "";
        }

        return getDisplayDate(convertedDate);
    }
}
