package cenergy.central.com.pwb_store.extensions

import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AlertDialog
import cenergy.central.com.pwb_store.R
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
        shippingMethods.contains(Product.PRODUCT_TWO_HOUR)
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

fun Activity?.getCompareProducts(): Int{
    val database = RealmController.getInstance()
    return database.compareProducts.size
}

fun Activity.showAlertDialog(message: String){
    val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_alert)) { dialog, _ ->
                dialog.dismiss()
            }
    builder.show()
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
    writeInt(when {
        realmList == null -> 0
        else -> 1
    })
    if (realmList != null) {
        writeInt(realmList.size)
        for (t in realmList) {
            writeParcelable(t, 0)
        }
    }
}

fun Parcel.writeIntList(input:List<Int>) {
    writeInt(input.size) // Save number of elements.
    input.forEach(this::writeInt) // Save each element.
}

fun Parcel.createIntList() : List<Int> {
    val size = readInt()
    val output = ArrayList<Int>(size)
    for (i in 0 until size) {
        output.add(readInt())
    }
    return output
}

fun Parcel.writeLongList(input: List<Long>) {
    writeInt(input.size) // Save number of elements.
    input.forEach(this::writeLong) // Save each element.
}

fun Parcel.createLongList(): List<Long> {
    val size = readLong()
    val output = ArrayList<Long>()
    for (i in 0 until size) {
        output.add(readLong())
    }
    return output
}

fun Parcel.writeRealmListLong(input:RealmList<Long>) {
    writeInt(input.size) // Save number of elements.
    input.forEach(this::writeLong) // Save each element.
}

fun Parcel.createRealmListLong() : RealmList<Long> {
    val size = readLong()
    val output = RealmList<Long>(size)
    for (i in 0 until size) {
        output.add(readLong())
    }
    return output
}

fun Parcel.readStringRealmList(): RealmList<String>? = when {
    readInt() > 0 -> RealmList<String>().also { list ->
        repeat(readInt()) {
            list.add(readString())
        }
    }
    else -> null
}

fun Parcel.writeStringRealmList(realmList: RealmList<String>?) {
    writeInt(when {
        realmList == null -> 0
        else -> 1
    })
    if (realmList != null) {
        writeInt(realmList.size)
        for (t in realmList) {
            writeString(t)
        }
    }
}