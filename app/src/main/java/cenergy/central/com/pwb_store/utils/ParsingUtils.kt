package cenergy.central.com.pwb_store.utils

import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.ProductResponse
import org.json.JSONObject

class ParsingUtils {

    companion object {
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
                val productIdChildren = arrayListOf<String>()
                val images = arrayListOf<ProductGallery>()
                val productOptions = arrayListOf<ProductOption>()
                val specifications = arrayListOf<Specification>()
                val pricingPerStore = arrayListOf<OfflinePriceItem>()
                val installmentPlans = arrayListOf<InstallmentPlan>()

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
                if (extensionObj.has("stock_item")) {
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

                if (extensionObj.has("ispu_salable")) {
                    stockItem.is2HProduct = extensionObj.getBoolean("ispu_salable")
                }
                if (extensionObj.has("salable")) {
                    stockItem.isSalable = extensionObj.getBoolean("salable")
                }
                if (extensionObj.has("pricing_per_store")) {
                    val attrArray = extensionObj.getJSONArray("pricing_per_store")
                    for (index in 0 until attrArray.length()) {
                        val offlinePriceObj = attrArray.getJSONObject(index)
                        val offlinePriceItem = OfflinePriceItem()
                        if (offlinePriceObj.has("entity_id")) {
                            offlinePriceItem.entityId = offlinePriceObj.getString("entity_id")
                        }
                        if (offlinePriceObj.has("product_id")) {
                            offlinePriceItem.productId = offlinePriceObj.getString("product_id")
                        }
                        if (offlinePriceObj.has("price")) {
                            offlinePriceItem.price = offlinePriceObj.getDouble("price")
                        }
                        if (offlinePriceObj.has("special_price") && !offlinePriceObj.isNull("special_price")) {
                            val specialPrice = offlinePriceObj.getString("special_price")
                            offlinePriceItem.specialPrice = if (specialPrice.trim() == "") 0.0 else specialPrice.toDouble()
                        }
                        if (offlinePriceObj.has("special_price_start") && !offlinePriceObj.isNull("special_price_start")) {
                            offlinePriceItem.specialFromDate = offlinePriceObj.getString("special_price_start")
                        }
                        if (offlinePriceObj.has("special_price_end") && !offlinePriceObj.isNull("special_price_end")) {
                            offlinePriceItem.specialToDate = offlinePriceObj.getString("special_price_end")
                        }
                        if (offlinePriceObj.has("retailer_id")) {
                            offlinePriceItem.retailerId = offlinePriceObj.getString("retailer_id")
                        }
                        if (offlinePriceObj.has("product_sku")) {
                            offlinePriceItem.productSku = offlinePriceObj.getString("product_sku")
                        }
                        if (offlinePriceObj.has("created_at")) {
                            offlinePriceItem.createdAt = offlinePriceObj.getString("created_at")
                        }
                        if (offlinePriceObj.has("updated_at")) {
                            offlinePriceItem.updatedAt = offlinePriceObj.getString("updated_at")
                        }
                        pricingPerStore.add(offlinePriceItem)
                    }
                }
                if (extensionObj.has("default_retailer_id")) {
                    productExtension.defaultRetailerId = extensionObj.getString("default_retailer_id")
                }
                if (extensionObj.has("installment_plans")){
                    val attrArray = extensionObj.getJSONArray("installment_plans")
                    for (index in 0 until attrArray.length()) {
                        val installmentPlanObj = attrArray.getJSONObject(index)
                        val installmentPlan = InstallmentPlan()
                        if (installmentPlanObj.has("period")){
                            installmentPlan.period = installmentPlanObj.getInt("period")
                        }
                        if (installmentPlanObj.has("interest_type")){
                            installmentPlan.interestType = installmentPlanObj.getString("interest_type")
                        }
                        if (installmentPlanObj.has("min_amount")){
                            installmentPlan.minAmount = installmentPlanObj.getInt("min_amount")
                        }
                        if (installmentPlanObj.has("valid_from")){
                            installmentPlan.validFrom = installmentPlanObj.getString("valid_from")
                        }
                        if (installmentPlanObj.has("active")){
                            val active = installmentPlanObj.getString("active").trim()
                            installmentPlan.active = active.toInt() == 1
                        }
                        if (installmentPlanObj.has("update")){
                            installmentPlan.update = installmentPlanObj.getString("update")
                        }
                        if (installmentPlanObj.has("bank")){
                            val bankObj = installmentPlanObj.getJSONObject("bank")
                            val bank = BankInstallment()
                            if (bankObj.has("id")){
                                bank.id = bankObj.getInt("bank_id")
                            }
                            if (bankObj.has("bank_image")){
                                bank.image = bankObj.getString("bank_image")
                            }
                            if (bankObj.has("name")){
                                bank.name = bankObj.getString("name")
                            }
                            if (bankObj.has("bank_icon")){
                                bank.icon = bankObj.getString("bank_icon")
                            }
                            if (bankObj.has("active")) {
                                val active = bankObj.getString("active").trim()
                                installmentPlan.active = active.toInt() == 1
                            }
                            if (bankObj.has("create")){
                                bank.create = bankObj.getString("create")
                            }
                            if (bankObj.has("update")){
                                bank.update = bankObj.getString("update")
                            }
                            if (bankObj.has("bank_color")){
                                bank.bankColor = bankObj.getString("bank_color")
                            }
                            installmentPlan.bank = bank
                        }
                        if (installmentPlanObj.has("valid_until")){
                            installmentPlan.validUntil = installmentPlanObj.getString("valid_until")
                        }
                        if (installmentPlanObj.has("bank_id")){
                            installmentPlan.bankId = installmentPlanObj.getInt("bank_id")
                        }
                        if (installmentPlanObj.has("name")){
                            installmentPlan.name = installmentPlanObj.getString("name")
                        }
                        if (installmentPlanObj.has("customer_rate")){
                            installmentPlan.customerRate = installmentPlanObj.getDouble("customer_rate")
                        }
                        if (installmentPlanObj.has("installmentplan_id")){
                            installmentPlan.id = installmentPlanObj.getInt("installmentplan_id")
                        }
                        if (installmentPlanObj.has("max_amount")){
                            installmentPlan.maxAmount = installmentPlanObj.getLong("max_amount")
                        }
                        if (installmentPlanObj.has("create")){
                            installmentPlan.create = installmentPlanObj.getString("create")
                        }
                        if (installmentPlanObj.has("currency")){
                            installmentPlan.currency = installmentPlanObj.getString("currency")
                        }
                        if (installmentPlanObj.has("merchant_rate")){
                            installmentPlan.merchantRate = installmentPlanObj.getDouble("merchant_rate")
                        }
                        installmentPlans.add(installmentPlan)
                    }
                }
                productExtension.pricingPerStore = pricingPerStore // add pricingPerStore to productExtension
                productExtension.installmentPlans = installmentPlans // add installmentPlans to productExtension
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
                                if (valueExtensionObject.has("frontend_type")) {
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
                    for (j in 0 until productChildrenArray.length()) {
                        productIdChildren.add(productChildrenArray.getString(j))
                    }
                }
                productExtension.productConfigLinks = productIdChildren

                val galleryArray = productObj.getJSONArray("media_gallery_entries")
                for (j in 0 until galleryArray.length()) {
                    val id = galleryArray.getJSONObject(j).getString("id")
                    val type = galleryArray.getJSONObject(j).getString("media_type")
                    var label = ""
                    if (galleryArray.getJSONObject(j).has("label")) {
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
                }
                productExtension.specifications = specifications // addd product spec to product extension
                products.add(product)
            }

            // get Filter Options
            val filterArray = productResponseObject.getJSONArray("filters")
            for (j in 0 until filterArray.length()) {
                val productFilter = ProductFilter()
                productFilter.name = filterArray.getJSONObject(j).getString("name")
                productFilter.code = filterArray.getJSONObject(j).getString("attribute_code")
                productFilter.position = filterArray.getJSONObject(j).getInt("position")
                val itemArray = filterArray.getJSONObject(j).getJSONArray("items")
                val filterItem = arrayListOf<FilterItem>()
                for (k in 0 until itemArray.length()) {
                    val filterItemObj = itemArray.getJSONObject(k)
                    val label = filterItemObj.getString("label")
                    val value = filterItemObj.getString("value")
                    val count = filterItemObj.getInt("count")
                    if (productFilter.code == "category_id" && filterItemObj.has("custom_attributes")) {
                        val customAttrFilterItemObj = filterItemObj.getJSONObject("custom_attributes")
                        if (customAttrFilterItemObj.has("level") && customAttrFilterItemObj.getInt("level") == 4) {
                            filterItem.add(FilterItem(label, value, count))
                        }
                    } else {
                        filterItem.add(FilterItem(label, value, count))
                    }
                }
                productFilter.items = filterItem
                filters.add(productFilter)
            }

            productResponse.products = products
            productResponse.filters = filters
            return productResponse
        }
    }
}