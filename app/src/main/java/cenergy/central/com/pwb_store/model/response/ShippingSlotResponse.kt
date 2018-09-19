package cenergy.central.com.pwb_store.model.response

class ShippingSlotResponse(
        var shippingSlot: ArrayList<ShippingSlot> = arrayListOf()
)

class ShippingSlot(
        var shippingDate: String = "",
        var slot: ArrayList<Slot> = arrayListOf()
)

class Slot(
        var id: Int = 0,
        var description: String = ""
)