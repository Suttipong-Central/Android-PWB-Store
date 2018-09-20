package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.interfaces.TimeSlotClickListener
import cenergy.central.com.pwb_store.model.response.Slot
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    var tvTimeSlot: PowerBuyTextView = itemView.findViewById(R.id.tv_time_slot)

    fun bindView(slot: Slot, timeSlotClickListener: TimeSlotClickListener?) {
        tvTimeSlot.text = slot.description
        itemView.setOnClickListener { timeSlotClickListener?.onTimeSlotClickListener(slot.id, slot.description) }
    }
}
