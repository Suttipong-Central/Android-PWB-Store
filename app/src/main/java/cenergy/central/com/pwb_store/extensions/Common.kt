package cenergy.central.com.pwb_store.extensions

import android.app.Activity
import android.content.Context
import android.support.v7.app.AlertDialog
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.realm.RealmController

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