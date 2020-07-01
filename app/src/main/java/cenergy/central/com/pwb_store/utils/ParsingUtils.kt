package cenergy.central.com.pwb_store.utils

import cenergy.central.com.pwb_store.Constants.Companion.DEFAULT_SOLD_BY
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.ProductResponse
import org.json.JSONObject

class ParsingUtils{

    companion object{
        @JvmStatic
        fun parseToProductResponse(response: okhttp3.Response): ProductResponse {
            val data = response.body()
            val productResponse = ProductResponse()
            val products = arrayListOf<Product>()
            val filters = arrayListOf<ProductFilter>()
            val productResponseObject = JSONObject(data?.string())
            productResponse.totalCount = productResponseObject.getInt("total_count")
            val productArray = productResponseObject.getJSONArray("products")
            for (i in 0 until productArray.length()) {
                val productObj = productArray.getJSONObject(i)
                val product = Product()
                val productExtension = ProductExtension()
                val stockItem = StockItem()
                val productFilter = ProductFilter()
                val productIdChildren = arrayListOf<String>()
                val images = arrayListOf<ProductGallery>()
                val productOptions = arrayListOf<ProductOption>()
                val specifications = arrayListOf<Specification>()
                val filterItem = arrayListOf<FilterItem>()
                val pricingPerStore = arrayListOf<OfflinePriceItem>()

                product.id = productObj.getLong("id")
                product.sku = productObj.getString("sku")
                product.name = productObj.getString("name")
                product.price = productObj.getDouble("price")
                product.status = productObj.getInt("status")

                if (productObj.has("brand")) {
                    product.brand = productObj.getString("brand")
                }

                if (productObj.has("brand_name")) {
                    product.brand = productObj.getString("brand_name")
                }

                if (!productObj.isNull("image")) {
                    product.image = productObj.getString("image")
                }

                val extensionObj = productObj.getJSONObject("extension_attributes")
                if(extensionObj.has("stock_item")){
                    val stockObject = extensionObj.getJSONObject("stock_item")
                    stockItem.productId = stockObject.getLong("product_id")
                    stockItem.stockId = stockObject.getLong("stock_id")
                    if (!stockObject.isNull("qty")) {
                        stockItem.qty = stockObject.getInt("qty")
                    }
                    stockItem.isInStock = stockObject.getBoolean("is_in_stock")
                    stockItem.maxQTY = stockObject.getInt("max_sale_qty")
                    stockItem.minQTY = stockObject.getInt("min_sale_qty")
                }

                if (extensionObj.has("ispu_salable")){
                    stockItem.is2HProduct = extensionObj.getBoolean("ispu_salable")
                }
                if (extensionObj.has("salable")){
                    stockItem.isSalable = extensionObj.getBoolean("salable")
                }
                if (extensionObj.has("pricing_per_store")){
                    val attrArray = extensionObj.getJSONArray("pricing_per_store")
                    for (index in 0 until attrArray.length()){
                        val offlinePriceObj = attrArray.getJSONObject(index)
                        val offlinePriceItem = OfflinePriceItem()
                        if (offlinePriceObj.has("entity_id")){
                            offlinePriceItem.entityId = offlinePriceObj.getString("entity_id")
                        }
                        if (offlinePriceObj.has("product_id")){
                            offlinePriceItem.productId = offlinePriceObj.getString("product_id")
                        }
                        if (offlinePriceObj.has("price")){
                            offlinePriceItem.price = offlinePriceObj.getDouble("price")
                        }
                        if (offlinePriceObj.has("special_price") && !offlinePriceObj.isNull("special_price")){
                            val specialPrice = offlinePriceObj.getString("special_price")
                            offlinePriceItem.specialPrice = if (specialPrice.trim() == "") 0.0 else specialPrice.toDouble()
                        }
                        if (offlinePriceObj.has("special_price_start") && !offlinePriceObj.isNull("special_price_start")){
                            offlinePriceItem.specialFromDate = offlinePriceObj.getString("special_price_start")
                        }
                        if (offlinePriceObj.has("special_price_end") && !offlinePriceObj.isNull("special_price_end")){
                            offlinePriceItem.specialToDate = offlinePriceObj.getString("special_price_end")
                        }
                        if (offlinePriceObj.has("retailer_id")){
                            offlinePriceItem.retailerId = offlinePriceObj.getString("retailer_id")
                        }
                        if (offlinePriceObj.has("product_sku")){
                            offlinePriceItem.productSku = offlinePriceObj.getString("product_sku")
                        }
                        if (offlinePriceObj.has("created_at")){
                            offlinePriceItem.createdAt = offlinePriceObj.getString("created_at")
                        }
                        if (offlinePriceObj.has("updated_at")){
                            offlinePriceItem.updatedAt = offlinePriceObj.getString("updated_at")
                        }
                        pricingPerStore.add(offlinePriceItem)
                    }
                }
                if (extensionObj.has("default_retailer_id")) {
                    productExtension.defaultRetailerId = extensionObj.getString("default_retailer_id")
                }

                productExtension.pricingPerStore = pricingPerStore
                productExtension.stokeItem = stockItem // add stockItem to productExtension
                product.extension = productExtension // set productExtension to product

                val attrArray = productObj.getJSONArray("custom_attributes_option")
                for (j in 0 until attrArray.length()) {
                    when (attrArray.getJSONObject(j).getString("attribute_code")) {
                        "special_price" -> {
                            if (!attrArray.getJSONObject(j).isNull("value")) {
                                val specialPrice = attrArray.getJSONObject(j).getString("value")
                                product.specialPrice = if (specialPrice.trim() == "") 0.0 else specialPrice.toDouble()
                            }
                        }

                        "special_from_date" -> {
                            if (!attrArray.getJSONObject(j).isNull("value")) {
                                product.specialFromDate = attrArray.getJSONObject(j).getString("value")
                            }
                        }

                        "special_to_date" -> {
                            if (!attrArray.getJSONObject(j).isNull("value")) {
                                product.specialToDate = attrArray.getJSONObject(j).getString("value")
                            }
                        }

                        "payment_methods" -> {
                            if (!attrArray.getJSONObject(j).isNull("value")) {
                                product.paymentMethod = attrArray.getJSONObject(j).getString("value")
                            }
                        }

                        "shipping_methods" -> {
                            if (!attrArray.getJSONObject(j).isNull("value")) {
                                product.shippingMethods = attrArray.getJSONObject(j).getString("value")
                            }
                        }
                    }

                    // set value to product specifications
                    val customAttrCode = attrArray.getJSONObject(j).getString("attribute_code")
                    specifications.forEach {
                        if (it.code == customAttrCode) {
                            val customAttrValue = attrArray.getJSONObject(j).getString("value")
                            it.value = customAttrValue
                        }
                    }
                }

                // get product specification
                if (extensionObj.has("specification_attributes")) {
                    val specAttrs = extensionObj.getJSONArray("specification_attributes")
                    for (specIndex in 0 until specAttrs.length()) {
                        val specAttr = specAttrs.getJSONObject(specIndex)
                        if (specAttr.has("attribute_code") && specAttr.has("label")) {
                            // no need attr star_rating
                            if (!specAttr.getString("attribute_code").contains("star_rating")) {
                                val attrCode = specAttr.getString("attribute_code")
                                val label = specAttr.getString("label")
                                specifications.add(Specification(code = attrCode, label = label))
                            }
                        }
                    }
                }

                if (extensionObj.has("configurable_product_options")) {
                    val productConfigArray = extensionObj.getJSONArray("configurable_product_options")
                    for (j in 0 until productConfigArray.length()) {
                        val id = productConfigArray.getJSONObject(j).getInt("id")
                        val attrId = productConfigArray.getJSONObject(j).getString("attribute_id")
                        val label = productConfigArray.getJSONObject(j).getString("label")
                        val position = productConfigArray.getJSONObject(j).getInt("position")
                        val productId = productConfigArray.getJSONObject(j).getLong("product_id")

                        val productValues = arrayListOf<ProductValue>()
                        if (productConfigArray.getJSONObject(j).has("values")) {
                            val valuesArray = productConfigArray.getJSONObject(j).getJSONArray("values")
                            for (k in 0 until valuesArray.length()) {
                                val index = valuesArray.getJSONObject(k).getInt("value_index")
                                val valueExtensionObject = valuesArray.getJSONObject(k).getJSONObject("extension_attributes")
                                var valueLabel = ""
                                if (valueExtensionObject.has("label")) {
                                    valueLabel = valueExtensionObject.getString("label")
                                }
                                var value = ""
                                if (valueExtensionObject.has("frontend_value")) {
                                    value = valueExtensionObject.getString("frontend_value")
                                }
                                var type = ""
                                if (valueExtensionObject.has("frontend_type")){
                                    type = valueExtensionObject.getString("frontend_type")
                                }
                                val productIDs = arrayListOf<Long>()
                                if (valueExtensionObject.has("products")) {
                                    val productIdArray = valueExtensionObject.getJSONArray("products")
                                    for (l in 0 until productIdArray.length()) {
                                        productIDs.add(productIdArray.getLong(l))
                                    }
                                }
                                productValues.add(ProductValue(index, ProductValueExtension(valueLabel, value, type, productIDs)))
                            }
                        }
                        productOptions.add(ProductOption(id, productId, attrId, label, position, productValues))
                    }
                }
                productExtension.productConfigOptions = productOptions

                if (extensionObj.has("configurable_product_links")) {
                    val productChildrenArray = extensionObj.getJSONArray("configurable_product_links")
                    for (j in 0 until productChildrenArray.length()){
                        productIdChildren.add(productChildrenArray.getString(j))
                    }
                }
                productExtension.productConfigLinks = productIdChildren

                val galleryArray = productObj.getJSONArray("media_gallery_entries")
                for (j in 0 until galleryArray.length()) {
                    val id = galleryArray.getJSONObject(j).getString("id")
                    val type = galleryArray.getJSONObject(j).getString("media_type")
                    var label = ""
                    if(galleryArray.getJSONObject(j).has("label")){
                        label = galleryArray.getJSONObject(j).getString("label")
                    }
                    var position = 0
                    if (!galleryArray.getJSONObject(j).isNull("position")) {
                        position = galleryArray.getJSONObject(j).getInt("position")
                    }
                    var disabled = false
                    if (!galleryArray.getJSONObject(j).isNull("disabled")) {
                        disabled = galleryArray.getJSONObject(j).getBoolean("disabled")
                    }
                    val file = galleryArray.getJSONObject(j).getString("file")
                    images.add(ProductGallery(id, type, label, position, disabled, file))
                }
                product.gallery = images

                val attrOptions = productObj.getJSONArray("custom_attributes_option")
                // set value to product specifications
                for (optionIndex in 0 until attrOptions.length()) {
                    val attrOption = attrOptions.getJSONObject(optionIndex)
                    val customAttrCode = attrOption.getString("attribute_code")
                    specifications.forEach {
                        if (it.code == customAttrCode) {
                            val customAttrValue = attrOption.getString("value")
                            it.value = customAttrValue
                        }
                    }
                    if (customAttrCode == "marketplace_seller"){
                        if (attrOption.getString("value").isNullOrEmpty()){
                            product.soldBy = DEFAULT_SOLD_BY
                        } else {
                            product.soldBy = attrOption.getString("value")
                        }
                    }
                }
                productExtension.specifications = specifications // addd product spec to product extension

                val filterArray = productResponseObject.getJSONArray("filters")
                for (j in 0 until filterArray.length()) {
                    when (filterArray.getJSONObject(j).getString("attribute_code")) {
                        "brand" -> {
                            productFilter.name = filterArray.getJSONObject(j).getString("name")
                            productFilter.code = filterArray.getJSONObject(j).getString("attribute_code")
                            productFilter.position = filterArray.getJSONObject(j).getInt("position")
                            val itemArray = filterArray.getJSONObject(j).getJSONArray("items")
                            for (k in 0 until itemArray.length()) {
                                val label = itemArray.getJSONObject(k).getString("label")
                                val value = itemArray.getJSONObject(k).getString("value")
                                val count = itemArray.getJSONObject(k).getInt("count")
                                filterItem.add(FilterItem(label, value, count))
                            }
                            productFilter.items = filterItem
                        }
                        "brand_name" -> {
                            productFilter.name = filterArray.getJSONObject(j).getString("name")
                            productFilter.code = filterArray.getJSONObject(j).getString("attribute_code")
                            productFilter.position = filterArray.getJSONObject(j).getInt("position")
                            val itemArray = filterArray.getJSONObject(j).getJSONArray("items")
                            for (k in 0 until itemArray.length()) {
                                val label = itemArray.getJSONObject(k).getString("label")
                                val value = itemArray.getJSONObject(k).getString("value")
                                val count = itemArray.getJSONObject(k).getInt("count")
                                filterItem.add(FilterItem(label, value, count))
                            }
                            productFilter.items = filterItem
                        }
                    }
                }
                filters.add(productFilter)
                products.add(product)
            }
            productResponse.products = products
            productResponse.filters = filters
            return productResponse
        }
    }
}