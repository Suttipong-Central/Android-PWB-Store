package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import android.webkit.URLUtil
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
class Product(
        var id: Long = 0,
        var sku: String = "",
        var name: String = "",
        var price: Double = 0.0,
        @SerializedName("type_id")
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
        var rating: Int? = 0,
        var shippingMethods: String = "",
        var paymentMethod: String = "",
        var isHDL: Boolean = false,
        @SerializedName("extension_attributes")
        var extension: ProductExtension? = null,
        private var productImageList: ProductDetailImage? = null,
        var urlKey: String = "") : IViewType, Parcelable {

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
            "$hostname$image"
        } else {
            image
        }
    }

    fun getMinSaleQty(): Int{
        return if (extension?.stokeItem?.minQTY != null && extension!!.stokeItem!!.minQTY!! > 0){
            extension!!.stokeItem!!.minQTY!!
        } else {
            1 // default qty is 1 when min sale qty is null or min sale qty < 1
        }
    }

    fun getPricePerStore(): OfflinePriceItem?{
        val db = RealmController.getInstance()
        val retailerId = db.userInformation?.store?.storeId?.toString()
        return if (extension != null && extension!!.pricingPerStore.isNotEmpty()) {
            extension!!.pricingPerStore.firstOrNull { it.retailerId == retailerId }
        } else {
            null
        }
    }

    companion object {
        const val PRODUCT_TWO_HOUR = "storepickup_ispu"
    }
}