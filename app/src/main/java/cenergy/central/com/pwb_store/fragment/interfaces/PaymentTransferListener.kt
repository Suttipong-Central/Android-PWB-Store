package cenergy.central.com.pwb_store.fragment.interfaces

interface PaymentTransferListener {
    fun startPayNow(payerName: String, payerEmail: String, agentCode: String,
                    agentChannelCode: String, mobileNumber: String)
}