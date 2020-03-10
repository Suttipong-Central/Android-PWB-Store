package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.response.BranchResponse
import com.google.gson.annotations.SerializedName

data class CartItemBody(var cartItem: CartBody? = null) {
    companion object {
        fun create(cartId: String, product: Product, retailerId: Int): CartItemBody {
            val cartExtensionAttr = CartExtensionAttr.create(retailerId)
            val cartBody = CartBody(cartId = cartId,sku =  product.sku,qty =  product.getMinSaleQty(),extensionAttr =  cartExtensionAttr)

            return CartItemBody(cartBody) // default add qty 1
        }

        fun create(cartId: String, product: Product, branchResponse: BranchResponse, retailerId: Int): CartItemBody {
            val shippingAssignment = ShippingAssignment(shippingMethod = "storepickup_ispu")
            val pickupStore = PickupStore(branchResponse.branch.storeId)
            val cartExtensionAttr = CartExtensionAttr.create(shippingAssignment = shippingAssignment,
                    pickupStore = pickupStore, retailerId = retailerId)
            val body = CartBody(cartId = cartId,
                    sku = product.sku,
                    qty = product.getMinSaleQty(),
                    extensionAttr = cartExtensionAttr)
            return CartItemBody(body)
        }
    }
}

data class CartBody(
        @SerializedName("quote_id")
        var cartId: String = "",
        var sku: String = "",
        var qty: Int? = 0,
        @SerializedName("product_option")
        var productOption: ProductOptionBody? = null,
        @SerializedName("extension_attributes")
        val extensionAttr: CartExtensionAttr? = null){
    companion object{
        fun create(cartId: String, sku: String, minSaleQty: Int, productOptionBody: ProductOptionBody): CartBody{
            return CartBody(cartId, sku, minSaleQty, productOptionBody)
        }

        fun create(cartId: String, sku: String, minSaleQty: Int): CartBody{
            return CartBody(cartId, sku, minSaleQty)
        }
    }
}

data class ProductOptionBody(
        @SerializedName("extension_attributes")
        var extension: OptionExtensionBody){
    companion object{
        fun create(optionExtensionBody: OptionExtensionBody): ProductOptionBody{
            return ProductOptionBody(optionExtensionBody)
        }
    }
}

data class OptionExtensionBody(
        @SerializedName("configurable_item_options")
        var configItemOption: ArrayList<OptionBody>? = null){
    companion object{
        fun create(configItemOption: ArrayList<OptionBody>): OptionExtensionBody{
            return OptionExtensionBody(configItemOption)
        }
    }
}

data class OptionBody(
        @SerializedName("option_id")
        var attributeId: String? = "",
        @SerializedName("option_value")
        var optionValue: Int? = 0)

data class CartExtensionAttr(
        @SerializedName("shipping_assignment")
        val shippingAssignment: ShippingAssignment? = null,
        @SerializedName("pickup_store")
        val pickupStore: PickupStore? = null,
        @SerializedName("allocated_store_id")
        val retailerId: Int){
    companion object{
        fun create(shippingAssignment: ShippingAssignment, pickupStore: PickupStore, retailerId: Int): CartExtensionAttr{
            return CartExtensionAttr(shippingAssignment, pickupStore, retailerId)
        }

        fun create(retailerId: Int): CartExtensionAttr{
            return CartExtensionAttr(retailerId = retailerId)
        }
    }
}

data class ShippingAssignment(
        @SerializedName("shipping_method")
        val shippingMethod: String = "")

data class PickupStore(
        @SerializedName("store_id")
        val storeId: String = "")