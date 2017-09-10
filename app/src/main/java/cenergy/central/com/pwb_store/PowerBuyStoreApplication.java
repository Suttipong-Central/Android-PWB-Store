package cenergy.central.com.pwb_store;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import cenergy.central.com.pwb_store.manager.Contextor;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class PowerBuyStoreApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Contextor.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}