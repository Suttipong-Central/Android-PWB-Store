package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.response.MemberResponse

interface PaymentMembersListener{
    fun getMembersList() : List<MemberResponse>
}