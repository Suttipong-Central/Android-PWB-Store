package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.AvailableStoreAdapter
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class StoreActive(
        @PrimaryKey
        var productSku: String = "",
        var stores: RealmList<StoreStock> = RealmList()
) : RealmObject() {

    companion object {
        const val TABLE_NAME = "StoreActive"
        const val FIELD_PRODUCT_SKU = "productSku"
        const val FIELD_STORES = "stores"
    }
}

open class StoreStock(
        var storeId: Int = 0,
        var qty: Int = 0,
        var name: String = "",
        var sellerCode: String = "",
        var contactPhone: String = "",
        @Ignore
        var isHighLight: Boolean = false
) : AvailableStoreAdapter.AvailableStoreItem, RealmObject() {
    companion object {
        const val TABLE_NAME = "StoreStock"
        const val FIELD_STORE_ID = "storeId"
        const val FIELD_STORE_NAME = "name"
        const val FIELD_QTY = "qty"
        const val FIELD_SELLER_CODE = "sellerCode"
        const val FIELD_CONTACT_PHONE = "contactPhone"
    }
}