package cenergy.central.com.pwb_store.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 8
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
                // add field isChatAndShopUser
                addField("company", String::class.java).setNullable("company", false)
                addField("vatId", String::class.java).setNullable("vatId", false)
            }
        }

        if (oldVersion < 4) {
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

            // Clear address data
            realm.delete("Province")
            realm.delete("District")
            realm.delete("SubDistrict")
            realm.delete("Postcode")

            // Update Province
            realm.schema.get("Province")?.apply {
                addField("name", String::class.java).setNullable("name", false)
                addField("defaultName", String::class.java).setNullable("defaultName", false)

                // change provinceId to String
                addField("provinceId_tmp", String::class.java).setNullable("provinceId_tmp", false)
                removeField("provinceId")
                renameField("provinceId_tmp", "provinceId")
                addPrimaryKey("provinceId")

                removeField("nameTh")
                removeField("nameEn")
            }

            // Update District
            realm.schema.get("District")?.apply {
                addField("name", String::class.java).setNullable("name", false)
                addField("defaultName", String::class.java).setNullable("defaultName", false)
                addField("code", String::class.java).setNullable("code", false)
                addField("provinceCode", String::class.java).setNullable("provinceCode", false)

                // change districtId to String
                addField("districtId_tmp", String::class.java).setNullable("districtId_tmp", false)
                removeField("districtId")
                renameField("districtId_tmp", "districtId")
                addPrimaryKey("districtId")

                // change provinceId to String
                addField("provinceId_tmp", String::class.java).setNullable("provinceId_tmp", false)
                removeField("provinceId")
                renameField("provinceId_tmp", "provinceId")

                removeField("nameTh")
                removeField("nameEn")
            }

            // Update SubDistrict
            realm.schema.get("SubDistrict")?.apply {
                addField("name", String::class.java).setNullable("name", false)
                addField("defaultName", String::class.java).setNullable("defaultName", false)
                addField("postcode", String::class.java).setNullable("postcode", false)
                addField("postcodeId", String::class.java).setNullable("postcodeId", false)
                addField("code", String::class.java).setNullable("code", false)
                addField("districtCode", String::class.java).setNullable("districtCode", false)

                // change subDistrictId to String
                addField("subDistrictId_tmp", String::class.java).setNullable("subDistrictId_tmp", false)
                removeField("subDistrictId")
                renameField("subDistrictId_tmp", "subDistrictId")
                addPrimaryKey("subDistrictId")

                // change districtId to String
                addField("districtId_tmp", String::class.java).setNullable("districtId_tmp", false)
                removeField("districtId")
                renameField("districtId_tmp", "districtId")

                removeField("nameTh")
                removeField("nameEn")
            }

            // Update Postcode
            realm.schema.get("Postcode")?.apply {
                // update postcodeId
                addField("postcodeId", String::class.java).setNullable("postcodeId", false)
                removeField("id")
                addPrimaryKey("postcodeId")

                // change postcode to String
                addField("postcode_tmp", String::class.java).setNullable("postcode_tmp", false)
                removeField("postcode")
                renameField("postcode_tmp", "postcode")

                // change postcode to String
                addField("subDistrictId_tmp", String::class.java).setNullable("subDistrictId_tmp", false)
                removeField("subDistrictId")
                renameField("subDistrictId_tmp", "subDistrictId")
            }
            realm.schema.get("Store")?.apply {
                addField("retailerId", String::class.java).setNullable("retailerId", false)
            }
        }

        if (oldVersion < 5) {
            // Update AddressInformation model
            realm.schema.get("AddressInformation")?.apply {
                // update same_as_billing to be optional
                setNullable("sameBilling", true)
            }
        }

        if (oldVersion < 6) {
            // Update SubAddress model
            realm.schema.get("SubAddress")?.apply {
                // add addressLine for street value**
                addField("addressLine", String::class.java).setNullable("addressLine", false)
            }
        }

        if (oldVersion < 7) {
            // Update Branch model
            realm.schema.get("Branch")?.apply {
                // Update attributes name address to street
                renameField("address", "street")
                addField("region", String::class.java).setNullable("region", false)
                addField("regionId", Int::class.java)
                addField("regionCode", String::class.java).setNullable("regionCode", false)
            }

            // Update SubAddress model
            realm.schema.get("SubAddress")?.apply {
                //set SubAddress can be null every attributes
                setNullable("mobile", true)
                setNullable("houseNumber", true)
                setNullable("building", true)
                setNullable("soi", true)
                setNullable("t1cNo", true)
                setNullable("district", true)
                setNullable("subDistrict", true)
                setNullable("postcode", true)
                setNullable("districtId", true)
                setNullable("subDistrictId", true)
                setNullable("postcodeId", true)
                setNullable("addressLine", true)
            }
        }

        if (oldVersion < 8) {
            // Update Order model
            realm.schema.get("Order")?.apply {
                // add payment redirect
                addField("paymentRedirect", String::class.java)
                        .setNullable("paymentRedirect", false)
            }
        }
    }
}