package cenergy.central.com.pwb_store

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.utils.RealmHelper.Companion.fallbackConfig
import cenergy.central.com.pwb_store.utils.RealmHelper.Companion.migrationConfig
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.exceptions.RealmMigrationNeededException

class PowerBuyStoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // setup Firebase Crashlytics
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        val kit = Crashlytics.Builder().core(core).build()
        Fabric.with(this, kit)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Contextor.getInstance().init(this)
        initRealm()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initRealm() {
        Realm.init(this) // add this line
        try {
            val realm = Realm.getInstance(migrationConfig())
            realm.close()
            Realm.setDefaultConfiguration(migrationConfig())
        } catch (e: RealmMigrationNeededException) {
            Log.e("RealmMigration", e.message)
            // Falback for release (delete realm on error)
            if (!BuildConfig.DEBUG) {
                PreferenceManager(this).userLogout() // clear cache share preference
                val realm = Realm.getInstance(fallbackConfig())
                realm.close()
            } else {
                throw RuntimeException("RealmMigration see log!.")
            }
        }
    }
}