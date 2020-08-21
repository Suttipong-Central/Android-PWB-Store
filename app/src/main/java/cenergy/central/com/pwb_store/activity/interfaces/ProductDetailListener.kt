package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.Installment
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.response.CreditCardPromotion

interface ProductDetailListener {
    fun getProduct(): Product?
    fun getChildProduct(): ArrayList<Product>
    fun getDeliveryInfoList():  List<DeliveryInfo>
    fun getBadgeSelects(): ArrayList<Int>
    fun getFreebieSKUs(): ArrayList<String>
    fun getFreeItems(): ArrayList<Product>
    fun getCreditCardPromotionList(): ArrayList<CreditCardPromotion>
    fun getInstallmentPlanList(): ArrayList<Installment>

    fun setDeliveryInfoList(deliveryInfos: List<DeliveryInfo>)
    fun setBadgeSelects(badgeSelects: ArrayList<Int>)
    fun setFreebieSKUs(freebieSKUs: ArrayList<String>)
    fun setFreeItems(freeItems: ArrayList<Product>)
    fun setCreditCardPromotionList(creditCardPromotionList: ArrayList<CreditCardPromotion>)
    fun setInstallmentPlanList(installmentPlanList: ArrayList<Installment>)

    fun addProductToCompare(product: Product?, isCompare: Boolean)
    fun addProductToCart(product: Product?)
    fun addProduct1HrsToCart(product: Product?)

    fun onDisplayAvailableStore(product: Product?)
    fun onDisplayOverview(overview: String)
    fun onDisplaySpecification(spec: String)

    fun onShareButtonClickListener()
}