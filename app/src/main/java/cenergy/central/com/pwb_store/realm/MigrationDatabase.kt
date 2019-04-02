package cenergy.central.com.pwb_store.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 0
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

    }
}