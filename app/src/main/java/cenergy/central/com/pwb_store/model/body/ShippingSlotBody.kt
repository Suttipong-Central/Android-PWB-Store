package cenergy.central.com.pwb_store.model.body

import com.google.gson.annotations.SerializedName

class ShippingSlotBody(
        @SerializedName("skus")
        var productHDLs: ArrayList<ProductHDLBody> = arrayListOf(),
        var district: String = "",
        var subDistrict: String = "",
        var province: String = "",
        var postalId: String = "",
        var period: PeriodBody,
        var customDetail: CustomDetail
) {
    companion object {
        fun createShippingSlotBody(productHDLs: ArrayList<ProductHDLBody>, district: String,
                                   subDistrict: String, province: String, postalId: String,
                                   period: PeriodBody, customDetail: CustomDetail): ShippingSlotBody {
            return ShippingSlotBody(productHDLs = productHDLs, district = district,
                    subDistrict = subDistrict, province = province, postalId = postalId,
                    period = period, customDetail = customDetail)
        }
    }
}

class ProductHDLBody(
        var id: String = "",
        var itemNo: Int = 0,
        var sku: String = "",
        @SerializedName("quantity")
        var qty: Int = 0,
        var fromStore: String = ""
) {
    companion object {
        fun createProductHDL(id: String, itemNo: Int, sku: String, qty: Int, fromStore: String): ProductHDLBody {
            return ProductHDLBody(id = id, itemNo = itemNo, sku = sku, qty = qty, fromStore = fromStore)
        }
    }
}

class PeriodBody(
        var year: Int = 0,
        var month: Int = 0
) {
    companion object {
        fun createPeriod(year: Int, month: Int): PeriodBody {
            return PeriodBody(year = year, month = month)
        }
    }
}

class CustomDetail(
        var deliveryType: String = "",
        var deliveryToStore: String = "",
        var deliveryByStore: String = ""
) {
    companion object {
        fun createCustomDetail(deliveryType: String, deliveryToStore: String, deliveryByStore: String): CustomDetail {
            return CustomDetail(deliveryType = deliveryType, deliveryToStore = deliveryToStore, deliveryByStore = deliveryByStore)
        }
    }
}