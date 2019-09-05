package cenergy.central.com.pwb_store.model

import android.webkit.URLUtil
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.model.response.ProductSearch
import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Product(
        var id: Int = 0,
        var sku: String = "",
        var name: String = "",
        var price: Double = 0.0,
        var typeId: String = "",
        @SerializedName("special_price")
        var specialPrice: Double = 0.0,
        @SerializedName("special_from_date")
        var specialFromDate: String? = null,
        @SerializedName("special_to_date")
        var specialToDate: String? = null,
        var brand: String = "",
        var image: String = "",
        var deliveryMethod: String = "",
        @SerializedName("media_gallery_entries")
        var gallery: List<ProductGallery> = arrayListOf(),
        var viewTypeID: Int = 0,
        var attributeID: Int = 0,
        var status: Int = 1,
        var shippingMethods: String = "",
        var paymentMethod: String = "",
        var isHDL: Boolean = false,
        @SerializedName("extension_attributes")
        var extension: ProductExtension? = null,
        private var productImageList: ProductDetailImage? = null) : IViewType {

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(viewTypeID: Int) {
        this.viewTypeID = viewTypeID
    }

    fun getDisplayOldPrice(unit: String): String {
        //return String.format(Locale.getDefault(), "%s %s", NumberFormat.getInstance(Locale.getDefault()).format(oldPrice), unit);
        return String.format(Locale.getDefault(), "%s %s", unit,
                NumberFormat.getInstance(Locale.getDefault()).format(java.lang.Double.parseDouble(price.toString())))
    }

    fun getDisplaySpecialPrice(unit: String): String {
        //return String.format(Locale.getDefault(), "%s %s", NumberFormat.getInstance(Locale.getDefault()).format(oldPrice), unit);
        return String.format(Locale.getDefault(), "%s %s", unit,
                NumberFormat.getInstance(Locale.getDefault()).format(java.lang.Double.parseDouble(specialPrice.toString())))
    }

    fun getProductImageList(): ProductDetailImage {
        if (productImageList == null) {
            val productDetailImageItems = ArrayList<ProductDetailImageItem>()
            val hostname = "${Constants.BASE_URL_MAGENTO}/media/catalog/product"
            for (image in gallery) {
                if (!image.file.contains(hostname, true)) {
                    productDetailImageItems.add(ProductDetailImageItem(image.id, "$hostname${image.file}"))
                } else {
                    image.file
                }
            }

            productImageList = ProductDetailImage(gallery.size, productDetailImageItems)
        }
        return productImageList as ProductDetailImage
    }

    fun getImageUrl(): String {
        val hostname = "${Constants.BASE_URL_MAGENTO}/media/catalog/product"
        return if (!URLUtil.isValidUrl(image)) {
//        return if (!image.contains(hostname, true)) {
            "$hostname$image"
        } else {
            image
        }
//        return image
    }

    fun isSpecialPrice(): Boolean {
        return if (specialFromDate != null && specialToDate != null) {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val specialFormDateTime = formatter.parse(specialFromDate)
            val specialToDateTime = formatter.parse(specialToDate)
            val current = Date()
            if (specialPrice < price) {
                val formatToday = SimpleDateFormat("dd", Locale.ENGLISH)
                val formatMonth = SimpleDateFormat("MM", Locale.ENGLISH)
                val formatYear = SimpleDateFormat("yyy", Locale.ENGLISH)
                if (formatToday.format(specialToDateTime) == formatToday.format(current)
                        && formatMonth.format(specialToDateTime) == formatMonth.format(current)
                        && formatYear.format(specialToDateTime) == formatYear.format(current)) {
                    true
                } else {
                    (current.time >= specialFormDateTime.time) && (current.time <= specialToDateTime.time)
                }
            } else {
                false
            }
        } else {
            false
        }
    }

    fun setProductImageList(productImageList: ProductDetailImage) {
        this.productImageList = productImageList
    }

    companion object {
        const val PRODUCT_TWO_HOUR = "storepickup_ispu"

        fun asProduct(product: ProductSearch): Product{
            return Product(id = product.id!!, sku = product.sku!!, price = product.price!!,
                    name = product.name!!, image = product.thumbnail?: "", brand = product.brand!!)
        }
    }
}