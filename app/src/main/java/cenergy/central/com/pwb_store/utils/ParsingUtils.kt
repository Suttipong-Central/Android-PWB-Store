package cenergy.central.com.pwb_store.utils

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
                val filterItem = arrayListOf<FilterItem>()

                product.id = productObj.getInt("id")
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
                val stockObject = extensionObj.getJSONObject("stock_item")
                stockItem.productId = stockObject.getLong("product_id")
                stockItem.stockId = stockObject.getLong("stock_id")
                if (!stockObject.isNull("qty")) {
                    stockItem.qty = stockObject.getInt("qty")
                }
                stockItem.isInStock = stockObject.getBoolean("is_in_stock")
                stockItem.maxQTY = stockObject.getInt("max_sale_qty")
                stockItem.minQTY = stockObject.getInt("min_sale_qty")
                if (extensionObj.has("ispu_salable")){
                    stockItem.is2HProduct = extensionObj.getBoolean("ispu_salable")
                }
                if (extensionObj.has("salable")){
                    stockItem.isSalable = extensionObj.getBoolean("salable")
                }
                productExtension.stokeItem = stockItem // add stockItem to productExtension
                product.extension = productExtension // set productExtension to product

                val attrArray = productObj.getJSONArray("custom_attributes")
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
                }

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