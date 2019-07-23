package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.AvailableStoreAdapter

class StoreAvailable(
        var qty: Int = 0,
        var name: String = "",
        var sellerCode: String = "",
        var contactPhone: String = "",
        var isHighLight: Boolean = false
) : AvailableStoreAdapter.AvailableStoreItem