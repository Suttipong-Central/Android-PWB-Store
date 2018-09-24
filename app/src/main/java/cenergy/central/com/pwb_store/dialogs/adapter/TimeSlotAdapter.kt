package cenergy.central.com.pwb_store.dialogs.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.viewholder.TimeSlotViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.TimeSlotClickListener
import cenergy.central.com.pwb_store.model.response.Slot

class TimeSlotAdapter(val slot: ArrayList<Slot>, private val timeSlotClickListener: TimeSlotClickListener?): RecyclerView.Adapter<TimeSlotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        return TimeSlotViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_time_slot, parent, false))
    }

    override fun getItemCount(): Int {
        return slot.size
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bindView(slot[position], timeSlotClickListener)
    }

}
