package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import android.webkit.URLUtil
import cenergy.central.com.pwb_store.Constants
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import java.text.NumberFormat
import java.util.*

@Parcelize
@RealmClass
open class Product(
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
        var gallery: @WriteWith<ProductGalleryRealmListParceler> RealmList<ProductGallery> = RealmList(),
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
        var urlKey: String = "") : IViewType, Parcelable, RealmModel {

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
            val productDetailImageItems = RealmList<ProductDetailImageItem>()
            val hostname = "${Constants.BASE_URL_MAGENTO}/media/catalog/product"
            for (image in gallery) {
                if (!image.file.contains(hostname, true)) {
                    productDetailImageItems.add(ProductDetailImageItem(productImageId = image.id, imgUrl = "$hostname${image.file}"))
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

    fun getMinSaleQty(): Int{
        return if (extension?.stokeItem?.minQTY != null && extension!!.stokeItem!!.minQTY!! > 0){
            extension!!.stokeItem!!.minQTY!!
        } else {
            1 // default qty is 1 when min sale qty is null or min sale qty < 1
        }
    }
    companion object {
        const val PRODUCT_TWO_HOUR = "storepickup_ispu"
    }
}