package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.response.MemberResponse

interface MemberClickListener{
    fun onClickedPwbMember(pwbMemberIndex: Int)
    fun onClickedT1CMember(member: MemberResponse)
}