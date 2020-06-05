package cenergy.central.com.pwb_store.realm

import cenergy.central.com.pwb_store.model.StoreActive
import cenergy.central.com.pwb_store.model.StoreStock
import io.realm.DynamicRealm
import io.realm.RealmMigration

class MigrationDatabase : RealmMigration {
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

        // app version 1.0.10.2
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
            // Update Branch model
            val branchSchema = realm.schema.get("Branch")?.apply {
                // Add ispuDelivery 'ispu_promise_delivery'
                addField("ispuDelivery", String::class.java)
            }

            // Update CacheCartItem model
            realm.schema.get("CacheCartItem")?.apply {
                // Add branch
                branchSchema?.let {
                    addRealmObjectField("branch", it)
                }
            }

            // Add StorePickupList model
            realm.schema.create("StorePickupList")?.apply {
                addField("sku", String::class.java)
                        .addPrimaryKey("sku")
                        .setNullable("sku", false)
                // Add list branch
                branchSchema?.let {
                    addRealmListField("stores", it)
                }

            }

            // Update Order model
            realm.schema.get("Order")?.apply {
                // Add discountPrice
                addField("discountPrice", Double::class.java)
                addField("total", Double::class.java)
            }

            // Update OrderResponse model
            realm.schema.get("OrderResponse")?.apply {
                // Add discount
                addField("discount", Double::class.java)
                addField("total", Double::class.java)
            }
        }

        // app version 1.1.1
        if (oldVersion < 9) {
            // Update Order model
            realm.schema.get("Order")?.apply {
                // add payment redirect
                addField("paymentRedirect", String::class.java)
                        .setNullable("paymentRedirect", false)

                // add t1c number
                addField("t1cEarnCardNumber", String::class.java)
                        .setNullable("t1cEarnCardNumber", false)
            }
        }

        // app version 1.1.1  
        if (oldVersion < 10) {
            // Create OrderCoupon
            val coupon = realm.schema.create("OrderCoupon").apply {
                addField("discountAmount", Double::class.java)
                addField("discountFormat", String::class.java)
                        .setNullable("discountFormat", false)
                addField("couponCode", String::class.java)
                        .setNullable("couponCode", false)
            }

            // Order add coupon object
            realm.schema.get("Order")?.apply {
                // add coupon
                addRealmObjectField("coupon", coupon)
            }

            // OrderResponse add coupon object
            realm.schema.get("OrderResponse")?.apply {
                realm.schema.get("OderExtension")?.apply {
                    addRealmObjectField("coupon", coupon)
                }
            }
        }

        if (oldVersion < 11) {
            // Remove Order Response Table
            realm.schema.remove("OrderResponse")

            // Order add field payment method
            realm.schema.get("Order")?.apply {
                // add paymentMethod
                addField("paymentMethod", String::class.java)
                        .setNullable("paymentMethod", false)
            }
        }

        if (oldVersion < 12) {
            // Update Product for get rating
            realm.schema.get("Product")?.apply {
                // add rating
                addField("rating", Int::class.java)
            }

            realm.schema.get("CompareProduct")?.apply {
                // add rating
                addField("rating", Int::class.java)
                // add min qty
                addField("minQty", Int::class.java)
                // add isSalable
                addField("isSalable", Boolean::class.java)
            }
        }

        if (oldVersion < 13) {
            realm.schema.get("User")?.apply {
                addField("userLevel", Long::class.java)
            }
        }

        // For OmniTV app version 0.0.1
        if (oldVersion < 14) {
            // create store stock
            val store = realm.schema.create(StoreStock.TABLE_NAME).apply {
                addField(StoreStock.FIELD_STORE_ID, Int::class.java)
                addField(StoreStock.FIELD_STORE_NAME, String::class.java)
                addField(StoreStock.FIELD_SELLER_CODE, String::class.java)
                addField(StoreStock.FIELD_QTY, Int::class.java)
                addField(StoreStock.FIELD_CONTACT_PHONE, String::class.java)
            }

            // create store active
            realm.schema.create(StoreActive.TABLE_NAME).apply {
                addField(StoreActive.FIELD_PRODUCT_SKU, String::class.java)
                addRealmObjectField(StoreActive.FIELD_STORES, store)
            }
        }
    }
}