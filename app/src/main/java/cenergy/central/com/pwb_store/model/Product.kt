package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Product() : Parcelable, IViewType {

    var id: Int = 0
    var sku: String = ""
    var name: String = ""
    var price: Double = 0.0
    @SerializedName("special_price")
    var specialPrice: Double = 0.0
    @SerializedName("special_from_date")
    var specialFromDate: String? = null
    @SerializedName("special_to_date")
    var specialToDate: String? = null
    var brand: String = ""
    @SerializedName("image")
    var imageUrl: String = ""
    var viewTypeID: Int = 0
    var attributeID: Int = 0
    var status: Int = 1
    @SerializedName("extension_attributes")
    var extension: ProductExtension? = null

    private var productImageList: ProductDetailImage? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        sku = parcel.readString()
        name = parcel.readString()
        price = parcel.readDouble()
        specialPrice = parcel.readDouble()
        brand = parcel.readString()
        imageUrl = parcel.readString()
        viewTypeID = parcel.readInt()
        attributeID = parcel.readInt()
        productImageList = parcel.readParcelable(ProductDetailImage::class.java.classLoader)
    }

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
            productDetailImageItems.add(ProductDetailImageItem("1", ""))
            productDetailImageItems.add(ProductDetailImageItem("2", ""))
            productDetailImageItems.add(ProductDetailImageItem("3", ""))
            productDetailImageItems.add(ProductDetailImageItem("4", ""))
            productDetailImageItems.add(ProductDetailImageItem("5", ""))
            productDetailImageItems.add(ProductDetailImageItem("6", ""))

            productImageList = ProductDetailImage(4, productDetailImageItems)
        }
        return productImageList as ProductDetailImage
    }

    fun isSpecialPrice(): Boolean {
        return if (specialFromDate != null && specialToDate != null) {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val specialFormDateTime = DateTime(formatter.parse(specialFromDate))
            val specialToDateTime = DateTime(formatter.parse(specialToDate))
            specialFormDateTime.isAfterNow && specialToDateTime.isBeforeNow
        } else {
            false
        }
    }

    fun setProductImageList(productImageList: ProductDetailImage) {
        this.productImageList = productImageList
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(sku)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeDouble(specialPrice)
        parcel.writeString(brand)
        parcel.writeString(imageUrl)
        parcel.writeInt(viewTypeID)
        parcel.writeInt(attributeID)
        parcel.writeParcelable(productImageList, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}