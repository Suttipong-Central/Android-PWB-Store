package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypeClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod

class PaymentMethodAdapter(private var listener: PaymentTypeClickListener) : RecyclerView.Adapter<PaymentMethodViewHolder>() {

    var paymentMethods = listOf<PaymentMethod>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        return PaymentMethodViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_payment_method, parent, false))
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bindView(paymentMethods[position], listener)
    }
}