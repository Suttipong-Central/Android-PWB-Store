package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.MembersViewHolder
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.model.PwbMember
import cenergy.central.com.pwb_store.model.response.MemberResponse

class MembersAdapter: RecyclerView.Adapter<MembersViewHolder>() {

    var memberList: List<Any> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var memberClickListener: MemberClickListener? = null

    fun setOnMemberClickListener(memberClickListener: MemberClickListener){
        this.memberClickListener = memberClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_members, parent, false))
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        when(getItemViewType(position)){
            PWBMember -> {
                holder.bindPwbMemberView(position, memberList[position] as PwbMember)
                holder.itemView.setOnClickListener { memberClickListener?.onClickedPwbMember(position) }
            }
            T1CMember -> {
                val member = memberList[position] as MemberResponse
                holder.bindT1CMemberView(position, member)
                holder.itemView.setOnClickListener { memberClickListener?.onClickedT1CMember(member.customerId) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (memberList[position] is PwbMember) {
            PWBMember
        } else {
            T1CMember
        }
    }

    companion object {
        private const val PWBMember = 0
        private const val T1CMember = 1
    }
}
