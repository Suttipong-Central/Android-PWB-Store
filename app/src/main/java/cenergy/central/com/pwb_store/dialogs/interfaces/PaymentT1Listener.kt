package cenergy.central.com.pwb_store.dialogs.interfaces

import cenergy.central.com.pwb_store.model.response.MemberResponse

interface PaymentT1Listener {
    fun onChangingT1Member(mobile: String)
    fun onSelectedT1Member(the1Member: MemberResponse)
}