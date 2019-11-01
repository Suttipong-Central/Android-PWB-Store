package cenergy.central.com.pwb_store;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager;
import cenergy.central.com.pwb_store.utils.RealmHelper;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

// TODO: refactor this class to be Kotlin and change class name
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
        try {
            Realm realm = Realm.getInstance(RealmHelper.Companion.migrationConfig());
            realm.close();
            Realm.setDefaultConfiguration(RealmHelper.Companion.migrationConfig());
        } catch (RealmMigrationNeededException e) {
            Log.e("RealmMigration", e.getMessage());
            // Falback for release (delete realm on error)
            if (!BuildConfig.DEBUG) {
                new PreferenceManager(this).userLogout(); // clear cache share preference
                Realm realm = Realm.getInstance(RealmHelper.Companion.fallbackConfig());
                realm.close();
            } else  {
                throw new RuntimeException("RealmMigration see log!.");
            }
        }
    }
}