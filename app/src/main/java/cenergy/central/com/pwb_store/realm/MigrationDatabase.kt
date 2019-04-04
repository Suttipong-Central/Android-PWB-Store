package cenergy.central.com.pwb_store.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 1
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        // Update Product model
        if (oldVersion < 1) {
           realm.schema.get("CacheCartItem")?.apply {
               // add field paymentMethod
               addField("paymentMethod", String::class.java).setNullable("paymentMethod", false)
           }
        }
    }
}