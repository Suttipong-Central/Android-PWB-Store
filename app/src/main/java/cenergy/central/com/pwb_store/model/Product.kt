package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import android.webkit.URLUtil
import cenergy.central.com.pwb_store.Constants
import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.createTypedArrayList(ProductGallery) ?: arrayListOf(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readByte() != 0.toByte(),
            parcel.readParcelable(ProductExtension::class.java.classLoader),
            parcel.readParcelable(ProductDetailImage::class.java.classLoader),
            parcel.readString() ?: "")

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

    fun getMinSaleQty(): Int{
        return if (extension?.stokeItem?.minQTY != null && extension!!.stokeItem!!.minQTY!! > 0){
            extension!!.stokeItem!!.minQTY!!
        } else {
            1 // default qty is 1 when min sale qty is null or min sale qty < 1
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(sku)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(typeId)
        parcel.writeDouble(specialPrice)
        parcel.writeString(specialFromDate)
        parcel.writeString(specialToDate)
        parcel.writeString(brand)
        parcel.writeString(image)
        parcel.writeString(deliveryMethod)
        parcel.writeTypedList(gallery)
        parcel.writeInt(viewTypeID)
        parcel.writeInt(attributeID)
        parcel.writeInt(status)
        parcel.writeString(shippingMethods)
        parcel.writeString(paymentMethod)
        parcel.writeByte(if (isHDL) 1 else 0)
        parcel.writeParcelable(extension, flags)
        parcel.writeParcelable(productImageList, flags)
        parcel.writeString(urlKey)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        const val PRODUCT_TWO_HOUR = "storepickup_ispu"

        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}