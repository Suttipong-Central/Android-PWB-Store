package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.MembersViewHolder
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.model.EOrderingMember
import cenergy.central.com.pwb_store.model.response.HDLCustomerInfos
import cenergy.central.com.pwb_store.model.response.MemberResponse

class MembersAdapter(private val showDetail: Boolean = false): RecyclerView.Adapter<MembersViewHolder>() {

    var memberList: List<Any> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var memberClickListener: MemberClickListener? = null

    fun setOnMemberClickListener(memberClickListener: MemberClickListener){
        this.memberClickListener = memberClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_members, parent, false), showDetail)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        when(getItemViewType(position)){
            HDLMember -> {
                holder.bindHDLMemberView(position, memberList[position] as HDLCustomerInfos)
                holder.itemView.setOnClickListener { memberClickListener?.onClickedHDLMember(position) }
            }
            PWBMember -> {
                holder.bindPwbMemberView(position, memberList[position] as EOrderingMember)
                holder.itemView.setOnClickListener { memberClickListener?.onClickedPwbMember(position) }
            }
            T1CMember -> {
                val member = memberList[position] as MemberResponse
                holder.bindT1CMemberView(position, member)
                holder.itemView.setOnClickListener { memberClickListener?.onClickedT1CMember(member) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(memberList[position]){
            is HDLCustomerInfos -> {
                HDLMember
            }
            is EOrderingMember -> {
                PWBMember
            }
            else -> {
                T1CMember
            }
        }
    }

    companion object {
        private const val HDLMember = 0
        private const val PWBMember = 1
        private const val T1CMember = 2
    }
}
