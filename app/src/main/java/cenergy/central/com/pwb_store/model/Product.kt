package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import java.text.NumberFormat
import java.util.*

class Product() : Parcelable,IViewType {

    var id: Int = 0
    var sku: String = ""
    var name: String = ""
    var attributeID: Int = 0
    var price: Double = 0.0
    var status: Int = 0
    var viewTypeID: Int = 0
    private var productImageList: ProductDetailImage? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        sku = parcel.readString()
        name = parcel.readString()
        attributeID = parcel.readInt()
        price = parcel.readDouble()
        status = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(sku)
        parcel.writeString(name)
        parcel.writeInt(attributeID)
        parcel.writeDouble(price)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(viewTypeID: Int) {
        this.viewTypeID = viewTypeID
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

    fun getDisplayOldPrice(unit: String): String {
        //return String.format(Locale.getDefault(), "%s %s", NumberFormat.getInstance(Locale.getDefault()).format(oldPrice), unit);
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(java.lang.Double.parseDouble(price.toString())))
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

    fun setProductImageList(productImageList: ProductDetailImage) {
        this.productImageList = productImageList
    }
}