package cenergy.central.com.pwb_store.utils

import cenergy.central.com.pwb_store.realm.MigrationDatabase
import io.realm.RealmConfiguration

/**
 * CRUD interface for Realm
 */

class RealmHelper {

    companion object {
        private const val schemaVersion = 9L
        private const val nameDb = "eordering.realm"

        fun migrationConfig(): RealmConfiguration {
            return RealmConfiguration.Builder().apply {
                name(nameDb)
                schemaVersion(schemaVersion)
                migration(MigrationDatabase())
            }.build()
        }

        fun fallbackConfig(): RealmConfiguration {
            return RealmConfiguration.Builder().apply {
                name(nameDb)
                schemaVersion(schemaVersion)
                deleteRealmIfMigrationNeeded()
            }.build()
        }
    }
}