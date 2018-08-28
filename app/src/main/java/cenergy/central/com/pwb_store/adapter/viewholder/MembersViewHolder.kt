package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.view.PowerBuyTextView

@SuppressLint("SetTextI18n")
class MembersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var listener: MemberClickListener? = null
    var layout: ConstraintLayout = itemView.findViewById(R.id.layout)
    var name: PowerBuyTextView = itemView.findViewById(R.id.member_name_list_members)

    fun bindView(position: Int, memberResponse: MemberResponse, context: Context) {
        name.text = "${position + 1}. ${memberResponse.getDisplayName()}"
        listener = context as MemberClickListener
        layout.setOnClickListener { listener?.onClickedMember(memberResponse.customerId) }
    }
}
