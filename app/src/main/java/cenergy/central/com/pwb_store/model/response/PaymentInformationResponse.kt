package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.model.CartTotal
import cenergy.central.com.pwb_store.model.PaymentMethod
import com.google.gson.annotations.SerializedName

data class PaymentInformationResponse(
        @SerializedName("payment_methods")
        var paymentMethods: ArrayList<PaymentMethod> = arrayListOf(),
        var totals: CartTotal? = null,
        @SerializedName("extension_attributes")
        val extension: PaymentInformationExtension? = null)

data class PaymentInformationExtension(
        @SerializedName("p2c2p_payment_agents")
        val paymentAgents: List<PaymentAgent> = listOf())

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

    fun isBankAgent() : Boolean {
        return PaymentAgentType.fromString(type) == PaymentAgentType.BANK
    }

    fun getChannels() : List<String> {
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