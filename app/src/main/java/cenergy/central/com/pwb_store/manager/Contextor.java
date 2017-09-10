package cenergy.central.com.pwb_store.manager;

import android.content.Context;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class Contextor {

    private static Contextor instance;
    private Context mContext;

    private Contextor() {

    }

    public static Contextor getInstance() {
        if (instance == null)
            instance = new Contextor();
        return instance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
