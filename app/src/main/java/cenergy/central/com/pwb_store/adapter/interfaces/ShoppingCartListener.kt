package cenergy.central.com.pwb_store.adapter.interfaces

interface ShoppingCartListener {
    fun onDeleteItem(itemId: Long, sku: String)
    fun onUpdateItem(itemId: Long, qty: Int, offlinePrice: Boolean)
}