package cenergy.central.com.pwb_store.realm

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
            realm.schema.get("CompareProduct")?.apply {
                // add rating
                addField("rating", Int::class.java)
            }
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
                realm.schema.get("OderExtension")?.apply{
                    addRealmObjectField("coupon", coupon)
                }
            }
        }

        if (oldVersion < 11) {
            val productDetailImageItem = realm.schema.create("ProductDetailImageItem").apply {
                addField("productImageId", String::class.java)
                addField("imgUrl", String::class.java)
                addField("viewTypeID", Int::class.java)
                addField("slug", String::class.java)
                addField("isSelected", Boolean::class.java)
            }

            val productDetailImage = realm.schema.create("ProductDetailImage").apply {
                addField("total", Int::class.java)
                addField("viewTypeID", Int::class.java)
                addRealmListField("productDetailImageItems", productDetailImageItem)
                addRealmObjectField("selectedProductDetailImageItem", productDetailImageItem)
            }

            val stockItem = realm.schema.create("StockItem").apply {
                addField("itemId", Long::class.java).setNullable("itemId", true)
                addField("productId", Long::class.java).setNullable("productId", true)
                addField("stockId", Long::class.java).setNullable("stockId", true)
                addField("qty", Int::class.java).setNullable("qty", true)
                addField("isInStock", Boolean::class.java)
                addField("maxQTY", Int::class.java).setNullable("maxQTY", true)
                addField("minQTY", Int::class.java).setNullable("minQTY", true)
                addField("is2HProduct", Boolean::class.java)
                addField("isSalable", Boolean::class.java)
            }

            val productValueExtension = realm.schema.create("ProductValueExtension").apply {
                addField("label", String::class.java)
                addField("value", String::class.java)
                addField("type", String::class.java)
                addRealmListField("products", Long::class.java).setNullable("products", true)
            }

            val productValue = realm.schema.create("ProductValue").apply {
                addField("index", Int::class.java)
                addRealmObjectField("valueExtension", productValueExtension)
            }

            val productOption = realm.schema.create("ProductOption").apply {
                addField("id", Int::class.java)
                addField("productId", Long::class.java)
                addField("attrId", String::class.java).setRequired("attrId", true)
                addField("label", String::class.java).setRequired("label", true)
                addField("position", Int::class.java)
                addRealmListField("values", productValue)
            }

            val specification = realm.schema.create("Specification").apply {
                addField("code", String::class.java).setRequired("code", true)
                addField("label", String::class.java).setRequired("label", true)
                addField("value", String::class.java)
            }

            val productExtension = realm.schema.create("ProductExtension").apply {
                addField("description", String::class.java)
                addField("shortDescription", String::class.java)
                addField("barcode", String::class.java)
                addRealmObjectField("stokeItem", stockItem)
                addRealmListField("productConfigOptions", productOption)
                addRealmListField("productConfigLinks", String::class.java)
                addRealmListField("specifications", specification)
            }

            val productGallery = realm.schema.create("ProductGallery").apply {
                addField("id", String::class.java).setRequired("id", true)
                addField("type", String::class.java)
                addField("label", String::class.java)
                addField("position", Int::class.java)
                addField("disabled", Boolean::class.java)
                addField("file", String::class.java).setRequired("file", true)
            }

            val categoryProduct = realm.schema.create("CategoryProduct").apply {
                addField("categoryId", Long::class.java)
                addField("name", String::class.java)
                addField("level", Int::class.java)
                addField("parentId", Long::class.java)
                addField("urlKey", String::class.java)
                addField("urlPath", String::class.java)
                addField("isParent", Boolean::class.java)
            }

            val product = realm.schema.create("Product").apply {
                addField("id", Long::class.java)
                addField("sku", String::class.java).setRequired("sku", true)
                addField("name", String::class.java).setRequired("name", true)
                addField("price", Double::class.java)
                addField("typeId", String::class.java).setRequired("typeId", true)
                addField("specialPrice", Double::class.java)
                addField("specialFromDate", String::class.java)
                addField("specialToDate", String::class.java)
                addField("rating", Int::class.java).setNullable("rating", true)
                addField("brand", String::class.java).setRequired("brand", true)
                addField("image", String::class.java).setRequired("image", true)
                addField("deliveryMethod", String::class.java).setRequired("deliveryMethod", true)
                addField("attributeID", Int::class.java)
                addField("status", Int::class.java)
                addField("shippingMethods", String::class.java).setRequired("shippingMethods", true)
                addField("paymentMethod", String::class.java).setRequired("paymentMethod", true)
                addField("isHDL", Boolean::class.java)
                addField("urlKey", String::class.java).setRequired("urlKey", true)
                addRealmObjectField("productImageList", productDetailImage)
                addRealmObjectField("extension", productExtension)
                addRealmListField("gallery", productGallery)
                addRealmListField("category", categoryProduct)
            }

            val filterItem = realm.schema.create("FilterItem").apply {
                addField("label", String::class.java)
                addField("value", String::class.java)
                addField("count", Int::class.java)
            }

            val productFilter = realm.schema.create("ProductFilter").apply {
                addField("name", String::class.java)
                addField("code", String::class.java)
                addRealmListField("items", filterItem)
                addField("name", String::class.java)
                addField("position", Int::class.java)
            }

            realm.schema.create("ProductResponse").apply {
                addRealmListField("products", product)
                addField("totalCount", Int::class.java)
                addRealmListField("filters", productFilter)
            }
        }
    }
}