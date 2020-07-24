package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.response.PromotionResponse

fun ArrayList<PromotionResponse>.toProductPromotions(): List<Pair<String, List<Int>>> {
    return this.map {
        val promotions = it.extension?.creditCardPromotions ?: arrayListOf()
        val discountAmounts = promotions.map { ccp ->
            ccp.discountAmount
        }
        Pair(it.sku, discountAmounts)
    }
}