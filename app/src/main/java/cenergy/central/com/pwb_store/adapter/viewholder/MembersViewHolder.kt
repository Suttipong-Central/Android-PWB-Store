package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.EOrderingMember
import cenergy.central.com.pwb_store.model.response.HDLCustomerInfos
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.view.PowerBuyTextView

@SuppressLint("SetTextI18n")
class MembersViewHolder(itemView: View, private val showDetail: Boolean) : RecyclerView.ViewHolder(itemView) {

    private var name: PowerBuyTextView = itemView.findViewById(R.id.member_name_list_members)
    private var tvThe1CarbNumber: PowerBuyTextView = itemView.findViewById(R.id.the1_card_number)

    fun bindHDLMemberView(position: Int, hdlCustomerInfos: HDLCustomerInfos) {
        name.text = "${position + 1}. ${hdlCustomerInfos.getDisplayName()}"
    }

    fun bindPwbMemberView(position: Int, EOrderingMember: EOrderingMember) {
        name.text = "${position + 1}. ${EOrderingMember.getDisplayName()}"
    }

    fun bindT1CMemberView(position: Int, memberResponse: MemberResponse) {
        if (showDetail) {
            tvThe1CarbNumber.visibility = View.VISIBLE
            tvThe1CarbNumber.text = memberResponse.cards[0].cardNo
        } else{
            tvThe1CarbNumber.visibility = View.GONE
        }
        name.text = "${position + 1}. ${memberResponse.getDisplayName()}"
    }
}