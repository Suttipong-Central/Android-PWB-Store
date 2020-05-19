package cenergy.central.com.pwb_store.adapter.interfaces

import cenergy.central.com.pwb_store.model.CompareProduct

interface CompareItemListener {
    fun onClickAddToCart(compareProduct: CompareProduct)
    fun onClearAllProductCompare()
}