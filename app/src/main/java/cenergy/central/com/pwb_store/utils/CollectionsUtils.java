package cenergy.central.com.pwb_store.utils;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class CollectionsUtils {
    public static Bundle toStringBundle(Map<String, String> input) {
        Bundle output = new Bundle();
        for (Map.Entry<String, String> entry : input.entrySet()) {
            output.putString(entry.getKey(), entry.getValue());
        }

        return output;
    }

    public static Map<String, String> fromStringBundle(Bundle input) {
        Map<String, String> output = new HashMap<>();
        for (String key : input.keySet()) {
            output.put(key, input.getString(key));
        }

        return output;
    }

    public static Bundle toBundle(Map<String, ? extends Parcelable> input) {
        Bundle output = new Bundle();
        for (String key : input.keySet()) {
            output.putParcelable(key, input.get(key));
        }
        return output;
    }

    public static <T extends Parcelable> Map<String, T> fromBundle(Bundle input, Class<T> c) {
        Map<String, T> output = new HashMap<String, T>();
        for (String key : input.keySet()) {
            output.put(key, c.cast(input.getParcelable(key)));
        }
        return output;
    }
}
