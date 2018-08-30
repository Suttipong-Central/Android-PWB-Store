package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Product
import com.google.gson.annotations.SerializedName

/*
*
* Create this model because response from api
* we got "extension_attributes" is arrayObject
*
 */
class ProductByBarcodeResponse(
        @SerializedName("items")
        var products: ArrayList<ProductByBarcode> = arrayListOf(),
        var currentPage: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0)

class ProductByBarcode(
        var id: Int = 0,
        var sku: String = "",
        var name: String = "")