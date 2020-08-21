package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.model.response.CreditCardPromotion

class ProductPromotion(
        var product: Product,
        var promotions: ArrayList<CreditCardPromotion> = arrayListOf()
)