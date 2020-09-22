package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.model.PaymentMethod
import com.google.gson.annotations.SerializedName

data class PaymentInformationResponse(
        @SerializedName("payment_methods")
        var paymentMethods: ArrayList<PaymentMethod> = arrayListOf(),
        @SerializedName("totals")
        var cartTotal: CartTotal? = null,
        @SerializedName("extension_attributes")
        val extension: PaymentInformationExtension? = null)

data class PaymentInformationExtension(
        @SerializedName("p2c2p_payment_agents")
        val paymentAgents: List<PaymentAgent> = listOf(),
        @SerializedName("p2c2p_credit_card_promotions")
        val paymentPromotions: List<PaymentCreditCardPromotion> = listOf()
)

data class PaymentCreditCardPromotion(
        @SerializedName("promotion_id")
        val promotionId: Int,
        val bank: Int,
        val description: String,
        @SerializedName("card_image")
        val cardImage: String,
        @SerializedName("card_type")
        val cardType: String,
        @SerializedName("card_name")
        val cardName: String,
        val banner: String,
        @SerializedName("promotion_code")
        val promotionCode: String,
        @SerializedName("payment_method")
        val paymentMethod: String,
        @SerializedName("ipp_plan")
        val ippPlan: String,
        @SerializedName("bank_color")
        val bankColor: String,
        @SerializedName("bank_icon")
        val bankIcon: String,
        @SerializedName("simple_action")
        val simpleAction: String,
        @SerializedName("discount_amount")
        val discountAmount: Int
) {
    companion object{
        const val DISCOUNT_BY_PERCENT = "by_percent"
        const val DISCOUNT_BY_FIXED = "by_fixed"
    }

    fun getBankImageUrl(): String {
        // example -> /media/banks/ktc22x22.jpg
        return "${Constants.BASE_URL_MAGENTO}/media/$bankIcon"
    }
}

data class PaymentAgent(
        @SerializedName("agent_id")
        val agentId: String,
        val name: String,
        val code: String,
        val type: String,
        val channel: String,
        @SerializedName("agent_image")
        val image: String) {
    fun getImageUrl(): String {
        // example -> /media/central_p2c2p123/BBL.png
        return "${Constants.BASE_URL_MAGENTO}/media/central_p2c2p123/$image"
    }

    fun isBankAgent(): Boolean {
        return PaymentAgentType.fromString(type) == PaymentAgentType.BANK
    }

    fun isCounterService(): Boolean {
        return PaymentAgentType.fromString(type) == PaymentAgentType.COUNTER_SERVICE
    }

    fun getChannels(): List<String> {
        return channel.split(",")
    }
}

enum class PaymentAgentType(val value: String) {
    BANK("BANK"), COUNTER_SERVICE("COUNTER");

    companion object {
        private val map = values().associateBy(PaymentAgentType::value)
        fun fromString(value: String) = map[value] ?: BANK
    }
}