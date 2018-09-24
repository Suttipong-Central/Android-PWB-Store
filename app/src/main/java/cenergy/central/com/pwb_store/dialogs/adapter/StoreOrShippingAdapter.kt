package cenergy.central.com.pwb_store.dialogs.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.viewholder.StoreOrShippingViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.StoreOrShippingClickListener
import cenergy.central.com.pwb_store.model.DialogOption

class StoreOrShippingAdapter(private val options: ArrayList<DialogOption>,
                             private val storeOrShippingClickListener: StoreOrShippingClickListener?)
    : RecyclerView.Adapter<StoreOrShippingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreOrShippingViewHolder {
        return StoreOrShippingViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_store_or_shipping, parent, false))
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: StoreOrShippingViewHolder, position: Int) {
        holder.onByView(options[position], storeOrShippingClickListener)
    }
}
