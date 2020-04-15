package cenergy.central.com.pwb_store.extensions

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.realm.RealmController
import io.realm.RealmList
import io.realm.RealmModel

fun Context?.isProductInStock(product: Product) : Boolean {
    val database = RealmController.getInstance()
    val productInCart = database.getCacheCartItemBySKU(product.sku)
    val productQty = product.extension?.stokeItem?.qty ?: 0

    return if (productInCart == null) {
        productQty > 0
    } else {
        val productInCartQty = productInCart.qty ?: 0
        productQty > productInCartQty
    }
}

fun Product.isSalable(): Boolean{
    return extension?.stokeItem?.isSalable != null && extension!!.stokeItem!!.isSalable
}

fun Product.is1HourProduct(): Boolean{
    return if (extension?.stokeItem?.is2HProduct != null && extension!!.stokeItem!!.is2HProduct &&
            shippingMethods.isNotEmpty()){
        shippingMethods.contains(Product.PRODUCT_ONE_HOUR)
    } else {
        false
    }
}

fun Context?.isProductInStock(compareProduct: CompareProduct) : Boolean {
    val database = RealmController.getInstance()
    val productInCart = database.getCacheCartItemBySKU(compareProduct.sku)
    val productQty = compareProduct.qtyInStock ?: 0

    return if (productInCart == null) {
        productQty > 0
    } else {
        val productInCartQty = productInCart.qty ?: 0
        productQty > productInCartQty
    }
}


fun <T> Parcel.readRealmList(clazz: Class<T>): RealmList<T>?
        where T : RealmModel,
              T : Parcelable = when {
    readInt() > 0 -> RealmList<T>().also { list ->
        repeat(readInt()) {
            list.add(readParcelable(clazz.classLoader))
        }
    }
    else -> null
}

fun <T> Parcel.writeRealmList(realmList: RealmList<T>?, clazz: Class<T>)
        where T : RealmModel,
              T : Parcelable {
    writeInt(when (realmList) {
        null -> 0
        else -> 1
    })
    if (realmList != null) {
        writeInt(realmList.size)
        for (t in realmList) {
            writeParcelable(t, 0)
        }
    }
}