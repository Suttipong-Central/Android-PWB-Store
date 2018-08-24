package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.CartItem

interface PaymentDescriptionListener{
    fun getItemList() : List<CartItem>
}