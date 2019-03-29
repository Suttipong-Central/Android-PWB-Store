package cenergy.central.com.pwb_store.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 1
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        if (oldVersion < 1L) {
            val personSchema = realm.schema.get("User")
            personSchema?.setNullable("storeId", true)
        }
    }
}