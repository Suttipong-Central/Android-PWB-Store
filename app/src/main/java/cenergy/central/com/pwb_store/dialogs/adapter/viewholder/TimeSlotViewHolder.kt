package cenergy.central.com.pwb_store.dialogs.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.interfaces.TimeSlotClickListener
import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var tvTimeSlot: PowerBuyTextView = itemView.findViewById(R.id.tv_time_slot)

    fun bindView(slot: ShippingSlot, timeSlotClickListener: TimeSlotClickListener?) {
        tvTimeSlot.text = slot.getTimeDescription()
        itemView.setOnClickListener { timeSlotClickListener?.onTimeSlotClickListener(slot) }
    }
}
