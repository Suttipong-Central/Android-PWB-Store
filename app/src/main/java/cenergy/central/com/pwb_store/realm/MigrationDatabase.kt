package cenergy.central.com.pwb_store.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 3
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        // Update Product model
        if (oldVersion < 1) {
            realm.schema.get("CacheCartItem")?.apply {
                // add field paymentMethod
                addField("paymentMethod", String::class.java).setNullable("paymentMethod", false)
            }
        }
        // Update User model
        if (oldVersion < 2) {
            realm.schema.get("User")?.apply {
                // add field isChatAndShopUser
                addField("isChatAndShopUser", Int::class.java)
            }
        }
        // Update AddressInformation model
        if (oldVersion < 3) {
            realm.schema.get("AddressInformation")?.apply {
                // add field city
                addField("city", String::class.java).setNullable("city", false)
            }
        }
    }
}