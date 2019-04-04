package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypeClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class PaymentMethodAdapter : RecyclerView.Adapter<SelectMethodViewHolder>() {

    var paymentMethods = listOf<PaymentMethod>()
    private lateinit var paymentTypeClickListener: PaymentTypeClickListener

    fun setPaymentTypeClickListener(paymentTypeClickListener: PaymentTypeClickListener){
        this.paymentTypeClickListener = paymentTypeClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectMethodViewHolder {
        return SelectMethodViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_payment_methods, parent, false))
    }

    override fun getItemCount(): Int {
        return paymentMethods.size
    }

    override fun onBindViewHolder(holder: SelectMethodViewHolder, position: Int) {
        holder.bindView(paymentMethods[position], paymentTypeClickListener)
    }
}

class SelectMethodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    var name: PowerBuyTextView = itemView.findViewById(R.id.text_method)

    fun bindView(paymentMethod: PaymentMethod, paymentTypeClickListener: PaymentTypeClickListener) {
        name.text = paymentMethod.title
        itemView.setOnClickListener { paymentTypeClickListener.onPaymentTypeClickListener(paymentMethod) }
    }
}