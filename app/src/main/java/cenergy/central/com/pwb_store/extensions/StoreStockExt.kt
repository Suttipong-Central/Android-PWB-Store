package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.StoreAvailable
import cenergy.central.com.pwb_store.realm.RealmController

fun List<StoreAvailable>.getStockAvailability(): Pair<Boolean, Boolean> {
    val userInformation = RealmController.getInstance().userInformation
    val retailerId = userInformation.store?.retailerId

    // output
    var stockCurrentStore = false
    val stockOtherStores = this.any { it.qty > 0 }

    if (retailerId != null) {
        val store = this.firstOrNull { it.sellerCode == retailerId }
        if (store != null) {
            stockCurrentStore = store.qty > 0
        }
    }

    return Pair(stockCurrentStore, stockOtherStores)
}