package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.MembersViewHolder
import cenergy.central.com.pwb_store.model.response.MemberResponse

class MembersAdapter(var context: Context) : RecyclerView.Adapter<MembersViewHolder>() {

    var memberList: List<MemberResponse> = listOf()
        set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_members, parent, false))
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        holder.bindView(position, memberList[position], context)
    }
}
