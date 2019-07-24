package cenergy.central.com.pwb_store.manager.listeners

interface MemberClickListener{
    fun onClickedPwbMember(pwbMemberIndex: Int)
    fun onClickedT1CMember(customerId: String, t1cardNo: String)
}