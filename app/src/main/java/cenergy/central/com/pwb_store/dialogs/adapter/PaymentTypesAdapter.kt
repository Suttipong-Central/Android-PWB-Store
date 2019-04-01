package cenergy.central.com.pwb_store.dialogs.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.adapter.viewholder.PaymentTypesViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypesClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod

class PaymentTypesAdapter(private val paymentMethods: ArrayList<PaymentMethod>,
                          private val paymentTypesClickListener: PaymentTypesClickListener?)
    : RecyclerView.Adapter<PaymentTypesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentTypesViewHolder {
        return PaymentTypesViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_playment_types, parent, false))
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun onBindViewHolder(holder: PaymentTypesViewHolder, position: Int) {
        holder.onByView(paymentMethods[position], paymentTypesClickListener)
    }
}
