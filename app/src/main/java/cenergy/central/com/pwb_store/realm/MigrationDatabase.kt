package cenergy.central.com.pwb_store.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 3
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        if (oldVersion < 1) {
            // Update Product model
            realm.schema.get("CacheCartItem")?.apply {
                // add field paymentMethod
                addField("paymentMethod", String::class.java).setNullable("paymentMethod", false)
            }
        }

        if (oldVersion < 2) {
            // Update User model
            realm.schema.get("User")?.apply {
                // add field isChatAndShopUser
                addField("isChatAndShopUser", Int::class.java)
            }
        }

        if (oldVersion < 3) {
            // Update AddressInformation model
            realm.schema.get("AddressInformation")?.apply {
                // add field city
                addField("city", String::class.java).setNullable("city", false)
            }

            // Update Category model
            realm.schema.get("Category")?.apply {
                removeField("mFilterHeaders")
                addField("parentId", String::class.java)
                addField("isActive", Boolean::class.java)
                addField("position", Int::class.java)
                addField("children", String::class.java)
                addField("createdAt", String::class.java)
                addField("updatedAt", String::class.java)
                addField("path", String::class.java)
            }

            // Update Branch
            realm.schema.get("Branch")?.apply {
                addField("isActive", Int::class.java)
                addField("sellerCode", String::class.java).setNullable("sellerCode", false)
                addField("attrSetName", String::class.java).setNullable("attrSetName", false)
                addField("createdAt", String::class.java).setNullable("createdAt", false)
                addField("updatedAt", String::class.java).setNullable("updatedAt", false)
                addField("fax", String::class.java).setNullable("fax", false)
            }
        }
    }
}