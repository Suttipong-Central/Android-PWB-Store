package cenergy.central.com.pwb_store;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;

import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.realm.MigrationDatabase;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class PowerBuyStoreApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Contextor.getInstance().init(this);
        initRealm();
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

    private void initRealm() {
        Realm.init(this);  // add this line
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("eordering.realm")
                .migration(new MigrationDatabase())
                .schemaVersion(MigrationDatabase.SCHEMA_VERSION)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}