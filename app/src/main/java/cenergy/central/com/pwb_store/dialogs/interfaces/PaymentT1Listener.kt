package cenergy.central.com.pwb_store.dialogs.interfaces

interface PaymentT1Listener {
    fun onChangingT1Member(mobile: String)
    fun onSelectedT1Member(t1cardNo: String)
}