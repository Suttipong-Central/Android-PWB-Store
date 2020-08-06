package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.BadgeSelectViewHolder

class BadgeSelectAdapter: RecyclerView.Adapter<BadgeSelectViewHolder>(){
    var badgeListener: BadgeListener? = null
    var badgesSelect: ArrayList<Int> = arrayListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var selectedPosition: Int = 0
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    fun setListener(context: Context){
        this.badgeListener = context as BadgeListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeSelectViewHolder {
        return BadgeSelectViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_badge_select, parent, false))
    }

    override fun getItemCount(): Int {
        return badgesSelect.size
    }

    override fun onBindViewHolder(holder: BadgeSelectViewHolder, position: Int) {
        holder.bind(badgesSelect[position], selectedPosition, badgeListener)
    }
}

interface BadgeListener{
    fun onBadgeSelectedListener(position: Int)
}